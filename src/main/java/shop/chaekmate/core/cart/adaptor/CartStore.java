package shop.chaekmate.core.cart.adaptor;

import java.util.List;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.model.CartItemSortCriteria;

public interface CartStore {
    Cart findCartByMemberId(Long cartId);
    Cart saveCart(Cart cart);
    void deleteCart(Long memberId);

    CartItem findItemById(Long cartItemId);
    List<CartItem> findItemList(Long cartId, CartItemSortCriteria criteria);
    CartItem saveOrUpdateItem(Long cartId, Long bookId, int quantity);
    void removeItem(Long cartItemId);
    void removeAllItem(Long cartId);
}
