package shop.chaekmate.core.cart.repository;

import java.util.List;
import shop.chaekmate.core.cart.entity.CartItem;

public interface CartItemRepositoryCustom {

    /**
     * 특정 Cart의 모든 장바구니 아이템 조회
     *
     * @param cartId Cart ID
     * @return 해당 Cart의 모든 장바구니 아이템 리스트
     */
    List<CartItem> findAllByCartId(Long cartId);
}
