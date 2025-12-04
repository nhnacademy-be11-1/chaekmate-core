package shop.chaekmate.core.cart.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CartRedisRepository {

    private static final String CART_PREFIX = "cart:";
    private static final String BOOK_PREFIX = "book:";
    private static final String OWNER_ID = "ownerId";
    private static final String CART_OWNER_PREFIX = "cart:owner:";
    private static final String CART_SEQ_KEY = "cart:id:seq";

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    /* ============================== Lua Scripts ============================== */

    /**
     * 장바구니 생성 Lua Script
     * - ID 생성, 장바구니 생성, 소유자 매핑을 원자적으로 처리
     */
    private static final String CREATE_CART_SCRIPT = """
            local seqKey = KEYS[1]
            local cartPrefix = KEYS[2]
            local ownerKey = KEYS[3]
            local ownerIdField = ARGV[1]
            local ownerId = ARGV[2]
            
            local cartId = redis.call('INCR', seqKey)
            if not cartId then
                return {err = 'ID 생성 실패'}
            end
            
            local cartKey = cartPrefix .. cartId
            redis.call('HSET', cartKey, ownerIdField, ownerId)
            redis.call('SET', ownerKey, cartId)
            
            return tostring(cartId)
            """;

    /**
     * 장바구니 아이템 추가/수정 Lua Script
     * - 장바구니 존재 여부 확인 후 아이템 추가/수정
     */
    private static final String PUT_CART_ITEM_SCRIPT = """
            local cartKey = KEYS[1]
            local ownerIdField = ARGV[1]
            local bookField = ARGV[2]
            local quantity = tonumber(ARGV[3])
            
            -- 장바구니 존재 여부 확인
            local exists = redis.call('HEXISTS', cartKey, ownerIdField)
            if exists == 0 then
                return {err = '장바구니가 존재하지 않습니다'}
            end
            
            -- 수량이 0 이하면 삭제
            if quantity <= 0 then
                redis.call('HDEL', cartKey, bookField)
                return 'DELETED'
            end
            
            -- 아이템 추가/수정
            redis.call('HSET', cartKey, bookField, quantity)
            return 'OK'
            """;

    /**
     * 장바구니 아이템 수량 증감 Lua Script
     * - 기존 수량에 delta를 더하거나 빼는 원자적 연산
     */
    private static final String INCREMENT_CART_ITEM_SCRIPT = """
            local cartKey = KEYS[1]
            local ownerIdField = ARGV[1]
            local bookField = ARGV[2]
            local delta = tonumber(ARGV[3])
            
            -- 장바구니 존재 여부 확인
            local exists = redis.call('HEXISTS', cartKey, ownerIdField)
            if exists == 0 then
                return {err = '장바구니가 존재하지 않습니다'}
            end
            
            -- 현재 수량 조회
            local current = redis.call('HGET', cartKey, bookField)
            local newQuantity = (current and tonumber(current) or 0) + delta
            
            -- 수량이 0 이하면 삭제
            if newQuantity <= 0 then
                redis.call('HDEL', cartKey, bookField)
                return 0
            end
            
            -- 수량 업데이트
            redis.call('HSET', cartKey, bookField, newQuantity)
            return newQuantity
            """;

    /**
     * 장바구니 아이템 삭제 Lua Script
     * - 장바구니 존재 여부 확인 후 삭제
     */
    private static final String DELETE_CART_ITEM_SCRIPT = """
            local cartKey = KEYS[1]
            local ownerIdField = ARGV[1]
            local bookField = ARGV[2]
            
            -- 장바구니 존재 여부 확인
            local exists = redis.call('HEXISTS', cartKey, ownerIdField)
            if exists == 0 then
                return {err = '장바구니가 존재하지 않습니다'}
            end
            
            -- 아이템 삭제
            local deleted = redis.call('HDEL', cartKey, bookField)
            return deleted
            """;

    /**
     * 장바구니 전체 아이템 삭제 Lua Script
     * - ownerId를 제외한 모든 book 아이템 삭제
     */
    private static final String DELETE_ALL_CART_ITEMS_SCRIPT = """
            local cartKey = KEYS[1]
            local ownerIdField = ARGV[1]
            local bookPrefix = ARGV[2]
            
            -- 장바구니 존재 여부 확인
            local exists = redis.call('HEXISTS', cartKey, ownerIdField)
            if exists == 0 then
                return {err = '장바구니가 존재하지 않습니다'}
            end
            
            -- 모든 필드 조회
            local fields = redis.call('HKEYS', cartKey)
            local deletedCount = 0
            
            for _, field in ipairs(fields) do
                if string.sub(field, 1, #bookPrefix) == bookPrefix then
                    redis.call('HDEL', cartKey, field)
                    deletedCount = deletedCount + 1
                end
            end
            
            return deletedCount
            """;

    /* ============================== 장바구니 ============================== */

    /**
     * 새로운 장바구니를 생성하고 ownerId와 연관된 key를 Redis에 저장함
     * Lua Script를 사용하여 원자적으로 처리
     *
     * @param ownerId 장바구니 소유자 ID
     * @return 생성된 장바구니 ID
     * @throws IllegalStateException Redis ID Sequence 생성 실패 시
     */
    public String createCart(String ownerId) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(CREATE_CART_SCRIPT);
        script.setResultType(String.class);

        String ownerKey = CART_OWNER_PREFIX + ownerId;

        String cartId = this.redisTemplate.execute(
                script,
                List.of(CART_SEQ_KEY, CART_PREFIX, ownerKey),
                OWNER_ID, ownerId
        );

        if (Objects.isNull(cartId)) {
            throw new IllegalStateException("Redis ID Sequence 생성 실패");
        }

        return cartId;
    }

    /**
     * 장바구니 ID로 소유자 ID를 조회함
     *
     * @param cartId 조회할 장바구니 ID
     * @return 장바구니 소유자 ID, 없으면 null
     */
    public String getOwnerId(String cartId) {
        String key = CART_PREFIX + cartId;
        Object value = this.hashOperations.get(key, OWNER_ID);
        return Objects.nonNull(value) ? value.toString() : null;
    }

    /**
     * 소유자 ID로 장바구니 ID를 조회함
     *
     * @param ownerId 장바구니 소유자 ID
     * @return 장바구니 ID, 없으면 null
     */
    public String findCartIdByOwner(String ownerId) {
        Object value = this.redisTemplate.opsForValue().get(CART_OWNER_PREFIX + ownerId);
        return Objects.nonNull(value) ? value.toString() : null;
    }

    /**
     * 장바구니 내 아이템 종류 개수를 조회함
     *
     * @param cartId 장바구니 ID
     * @return 장바구니 내 도서 종류 개수
     */
    public int getCartItemSize(String cartId) {
        String key = CART_PREFIX + cartId;
        long size = this.hashOperations.size(key);
        return (size > 0) ? (int) (size - 1) : 0;
    }

    /* =========================== 장바구니 아이템 =========================== */

    /**
     * 장바구니에 아이템을 추가하거나 수량을 수정함
     * Lua Script를 사용하여 원자적으로 처리
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     * @param quantity 수량
     * @throws IllegalStateException 장바구니가 존재하지 않을 경우
     */
    public void putCartItem(String cartId, Long bookId, int quantity) {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setScriptText(PUT_CART_ITEM_SCRIPT);
        script.setResultType(String.class);

        String cartKey = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;

        String result = this.redisTemplate.execute(
                script,
                List.of(cartKey),
                OWNER_ID, bookField, quantity
        );

        if (Objects.isNull(result) || result.startsWith("{err")) {
            throw new IllegalStateException("장바구니가 존재하지 않습니다");
        }
    }

    /**
     * 장바구니 아이템 수량을 증감함 (원자적 연산)
     * 주문 수량 증가/감소, 재고 복구 등에 활용
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     * @param delta 증감할 수량 (양수: 증가, 음수: 감소)
     * @return 변경 후 수량 (0이면 삭제됨)
     * @throws IllegalStateException 장바구니가 존재하지 않을 경우
     */
    // 실제 사용X
    public int incrementCartItem(String cartId, Long bookId, int delta) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(INCREMENT_CART_ITEM_SCRIPT);
        script.setResultType(Long.class);

        String cartKey = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;

        Long result = this.redisTemplate.execute(
                script,
                List.of(cartKey),
                OWNER_ID, bookField, delta
        );

        if (Objects.isNull(result)) {
            throw new IllegalStateException("장바구니가 존재하지 않습니다");
        }

        return result.intValue();
    }

    /**
     * 장바구니 내 특정 아이템을 조회함 (수량 조회)
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     * @return 수량, 없으면 null
     */
    public Integer getCartItem(String cartId, Long bookId) {
        String key = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;
        Object value = this.hashOperations.get(key, bookField);
        return Objects.nonNull(value) ? Integer.parseInt(value.toString()) : null;
    }

    /**
     * 장바구니 내 모든 아이템을 조회함 (수량 조회)
     *
     * @param cartId 장바구니 ID
     * @return Map<bookId, quantity>
     */
    public Map<Long, Integer> getAllCartItems(String cartId) {
        String key = CART_PREFIX + cartId;
        Map<String, Object> entries = this.hashOperations.entries(key);
        return this.parseBookFields(entries);
    }

    /**
     * 장바구니 내 특정 아이템을 삭제함
     * Lua Script를 사용하여 원자적으로 처리
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     * @throws IllegalStateException 장바구니가 존재하지 않을 경우
     */
    public void deleteCartItem(String cartId, Long bookId) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(DELETE_CART_ITEM_SCRIPT);
        script.setResultType(Long.class);

        String cartKey = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;

        Long result = this.redisTemplate.execute(
                script,
                List.of(cartKey),
                OWNER_ID, bookField
        );

        if (Objects.isNull(result)) {
            throw new IllegalStateException("장바구니가 존재하지 않습니다");
        }
    }

    /**
     * 장바구니 내 모든 아이템을 삭제함
     * Lua Script를 사용하여 원자적으로 처리
     *
     * @param cartId 장바구니 ID
     * @return 삭제된 아이템 개수
     * @throws IllegalStateException 장바구니가 존재하지 않을 경우
     */
    public int deleteAllCartItems(String cartId) {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setScriptText(DELETE_ALL_CART_ITEMS_SCRIPT);
        script.setResultType(Long.class);

        String cartKey = CART_PREFIX + cartId;

        Long result = this.redisTemplate.execute(
                script,
                List.of(cartKey),
                OWNER_ID, BOOK_PREFIX
        );

        if (Objects.isNull(result)) {
            throw new IllegalStateException("장바구니가 존재하지 않습니다");
        }

        return result.intValue();
    }

    /* =========================== 내부 유틸 메서드 =========================== */

    /**
     * Redis에서 조회한 Hash entries를 "bookId → quantity" 형식으로 변환함
     *
     * @param entries Redis hash entries (field → value)
     * @return Map<bookId, quantity>
     */
    private Map<Long, Integer> parseBookFields(Map<String, Object> entries) {
        Map<Long, Integer> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();

            if (!field.startsWith(BOOK_PREFIX)) {
                continue;
            }

            Long bookId = Long.parseLong(field.substring(BOOK_PREFIX.length()));

            Object value = entry.getValue();
            Integer quantity;
            if (value instanceof Integer intValue) {
                quantity = intValue;
            } else {
                quantity = Integer.parseInt(value.toString());
            }

            result.put(bookId, quantity);
        }

        return result;
    }
}