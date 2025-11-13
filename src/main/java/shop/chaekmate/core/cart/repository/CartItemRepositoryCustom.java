package shop.chaekmate.core.cart.repository;

import java.util.List;
import shop.chaekmate.core.cart.entity.CartItem;

public interface CartItemRepositoryCustom {
    List<CartItem> findAllByCartIdOrderByBookTitleAsc(Long cartId);
    List<CartItem> findAllByCartIdOrderByBookTitleDesc(Long cartId);
}
