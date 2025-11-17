package shop.chaekmate.core.cart.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
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

    /* TTL 적용X --> 영구 저장 */
    /* TTL 적용  --> redisTemplate 활용 : key 단위로 관리 --> key 생성 시 expire 옵션 추가 */

    /* ============================== 장바구니 ============================== */
    /**
     * 새로운 장바구니를 생성하고 ownerId와 연관된 key를 Redis에 저장함
     * @param ownerId 장바구니 소유자 ID
     * @return 생성된 장바구니 ID
     * @throws IllegalStateException Redis ID Sequence 생성 실패 시
     */
    public String createCart(String ownerId) {
        Long cartIdLong = this.redisTemplate.opsForValue().increment(CART_SEQ_KEY);
        if (Objects.isNull(cartIdLong)) {
            throw new IllegalStateException("Redis ID Sequence 생성 실패");
        }

        String cartId = cartIdLong.toString();

        String key = CART_PREFIX + cartId;
        this.hashOperations.put(key, OWNER_ID, ownerId);   // HSET (key --> ownerId)
        this.redisTemplate.opsForValue().set(CART_OWNER_PREFIX + ownerId, cartId);  // HSET (ownerId --> cartId 매핑)

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

    /* =========================== 장바구니 아이템 =========================== */

    /**
     * 장바구니에 아이템을 추가하거나 수량을 수정함
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     * @param quantity 수량
     */
    public void putCartItem(String cartId, Long bookId, int quantity) {
        String key = CART_PREFIX + cartId;
        String bookField  = BOOK_PREFIX + bookId;
        this.hashOperations.put(key, bookField, quantity);  // HSET
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
        Object value = this.hashOperations.get(key, bookField);     // HGET

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
        Map<String, Object> entries = this.hashOperations.entries(key); // HGETALL
        return this.parseBookFields(entries);   // bookId --> quantity 형태로 반환
    }

    /**
     * 장바구니 내 특정 아이템을 삭제함
     *
     * @param cartId 장바구니 ID
     * @param bookId 도서 ID
     */
    public void deleteCartItem(String cartId, Long bookId) {
        String key = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;
        this.hashOperations.delete(key, bookField); // HDEL
    }

    /**
     * 장바구니 내 모든 아이템을 삭제함
     *
     * @param cartId 장바구니 ID
     */
    public void deleteAllCartItems(String cartId) {
        String key = CART_PREFIX + cartId;
        Set<String> fields = this.hashOperations.keys(key); // HKEYS
        for (String field : fields) {
            if (field.startsWith(BOOK_PREFIX)) {
                this.hashOperations.delete(key, field); // HDEL
            }
        }
    }

    /* =========================== 내부 유틸 메서드 =========================== */

    /**
     * Redis에서 조회한 Hash entries 를 "bookId → quantity" 형식으로 변환함
     *
     * <p>특징:
     * 1. "ownerId" 필드는 제외
     * 2. Object 타입을 Integer 로 변환
     *
     * @param entries Redis hash entries (field → value)
     * @return Map<bookId, quantity>
     */
    private Map<Long, Integer> parseBookFields(Map<String, Object> entries) {
        Map<Long, Integer> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();

            // ownerId FIELD 제외
            if (!field.startsWith(BOOK_PREFIX)) {
                continue;
            }

            Long bookId = Long.parseLong(field.substring(5));   // "bookId:{bookId}" → {bookId}

            Object value = entry.getValue();
            Integer quantity;
            if (value instanceof Integer intValue) {
                quantity = intValue;    // Integer 일 경우, 그대로 사용
            } else {
                quantity = Integer.parseInt(value.toString());  // Integer 아닐 경우, Integer 변환
            }

            result.put(bookId, quantity);
        }

        return result;
    }
}
