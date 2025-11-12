package shop.chaekmate.core.cart.adaptor;

import java.util.List;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.model.CartItemSortCriteria;

public interface CartStore {
    Cart findCartByMemberId(Long memberId);
    Cart saveCart(Cart cart);
    void deleteCart(Cart cart);
    List<CartItem> findItemList(Long cartId, CartItemSortCriteria criteria);
    CartItem addItem(Long cartId, Long bookId, int quantity);
    void removeItem(Long cartId, Long bookId);
    void removeAllItem(Long cartId);
}
