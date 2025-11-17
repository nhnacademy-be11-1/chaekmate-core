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

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;

    /* TTL 적용X --> 영구 저장 */
    /* TTL 적용  --> redisTemplate 활용 : key 단위로 관리 --> key 생성 시 expire 옵션 추가 */

    /* 장바구니 */
    // 장바구니 생성
    public void createCart(String cartId, String ownerId) {
        String key = CART_PREFIX + cartId;
        this.hashOperations.put(key, "ownerId", ownerId);   // HSET
    }

    // 장바구니 소유자 조회
    public String getOwnerId(String cartId) {
        String key = CART_PREFIX + cartId;
        Object value = hashOperations.get(key, "ownerId");
        return Objects.nonNull(value) ? value.toString() : null;
    }

    /* 장바구니 아이템 */
    // 장바구니 아이템 추가/수정(수량)
    public void putCartItem(String cartId, Long bookId, int quantity) {
        String key = CART_PREFIX + cartId;
        String bookField  = BOOK_PREFIX + bookId;
        this.hashOperations.put(key, bookField, quantity);  // HSET
    }

    // 장바구니 아이템 단일 조회
    public Integer getCartItem(String cartId, Long bookId) {
        String key = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;
        Object value = this.hashOperations.get(key, bookField);     // HGET

        return Objects.nonNull(value) ? Integer.parseInt(value.toString()) : null;
    }

    // 장바구니 아이템 전체 조회
    public Map<Long, Integer> getAllCartItems(String cartId) {
        String key = CART_PREFIX + cartId;
        Map<String, Object> entries = this.hashOperations.entries(key); // HGETALL
        return this.parseBookFields(entries);
    }

    // 장바구니 아이템 단일 삭제
    public void deleteCartItem(String cartId, Long bookId) {
        String key = CART_PREFIX + cartId;
        String bookField = BOOK_PREFIX + bookId;
        this.hashOperations.delete(key, bookField); // HDEL
    }

    // 장바구니 아이템 전체 삭제
    public void deleteAllCartItems(String cartId) {
        String key = CART_PREFIX + cartId;
        Set<String> fields = this.hashOperations.keys(key); // HKEYS
        for (String field : fields) {
            if (field.startsWith(BOOK_PREFIX)) {
                this.hashOperations.delete(key, field); // HDEL
            }
        }
    }


    private Map<Long, Integer> parseBookFields(Map<String, Object> entries) {
        Map<Long, Integer> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : entries.entrySet()) {
            String field = entry.getKey();

            // ownerId FIELD 제외
            if (!field.startsWith(BOOK_PREFIX)) {
                continue;
            }

            Long bookId = Long.parseLong(field.substring(5));

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
