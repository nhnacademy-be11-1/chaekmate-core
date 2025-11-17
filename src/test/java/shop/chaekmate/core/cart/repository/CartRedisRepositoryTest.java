package shop.chaekmate.core.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CartRedisRepositoryTest {

    @Autowired
    private CartRedisRepository cartRedisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private String cartId;

    @AfterEach
    void cleanUp() {
        if (Objects.nonNull(cartId)) {
            String key = "cart:" + cartId;
            redisTemplate.delete(key);
        }
    }

    @Test
    void 장바구니_생성_및_조회_성공() {
        cartId = cartRedisRepository.createCart("1");

        String ownerId = cartRedisRepository.getOwnerId(cartId);
        assertThat(ownerId).isEqualTo("1");
    }

    @Test
    void 소유자_기준_장바구니_조회_성공() {
        cartId = cartRedisRepository.createCart("1");

        String foundCartId = cartRedisRepository.findCartIdByOwner("1");

        assertThat(foundCartId).isEqualTo(cartId);
    }

    @Test
    void 존재하지_않는_소유자_기준_조회_시_null_반환() {
        String foundCartId = cartRedisRepository.findCartIdByOwner("non-existent-owner");

        assertThat(foundCartId).isNull();
    }

    @Test
    void 장바구니_아이템_추가_및_단일_조회_성공() {
        cartId = cartRedisRepository.createCart("1");

        cartRedisRepository.putCartItem(cartId, 101L, 2);
        cartRedisRepository.putCartItem(cartId, 102L, 5);

        Integer qty101 = cartRedisRepository.getCartItem(cartId, 101L);
        Integer qty102 = cartRedisRepository.getCartItem(cartId, 102L);

        assertThat(qty101).isEqualTo(2);
        assertThat(qty102).isEqualTo(5);
    }

    @Test
    void 장바구니_아이템_전체_조회_성공() {
        cartId = cartRedisRepository.createCart("1");
        cartRedisRepository.putCartItem(cartId, 101L, 2);
        cartRedisRepository.putCartItem(cartId, 102L, 5);

        Map<Long, Integer> allItems = cartRedisRepository.getAllCartItems(cartId);

        assertThat(allItems).hasSize(2)
                            .containsEntry(101L, 2)
                            .containsEntry(102L, 5);
    }

    @Test
    void 장바구니_아이템_단일_삭제_성공() {
        cartId = cartRedisRepository.createCart("1");
        cartRedisRepository.putCartItem(cartId, 101L, 2);

        cartRedisRepository.deleteCartItem(cartId, 101L);

        Integer qty = cartRedisRepository.getCartItem(cartId, 101L);
        assertThat(qty).isNull();
    }

    @Test
    void 장바구니_아이템_전체_삭제_성공() {
        cartId = cartRedisRepository.createCart("1");
        cartRedisRepository.putCartItem(cartId, 101L, 2);
        cartRedisRepository.putCartItem(cartId, 102L, 5);

        cartRedisRepository.deleteAllCartItems(cartId);

        Map<Long, Integer> items = cartRedisRepository.getAllCartItems(cartId);
        assertThat(items).isEmpty();
    }
}

