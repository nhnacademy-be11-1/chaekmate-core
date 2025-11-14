package shop.chaekmate.core.cart.exception.cartitem;

import shop.chaekmate.core.common.exception.CoreException;

public class CartItemNotFoundException extends CoreException {
    public CartItemNotFoundException() {
        super(CartItemErrorCode.CART_ITEM_NOT_FOUND);
    }
}
