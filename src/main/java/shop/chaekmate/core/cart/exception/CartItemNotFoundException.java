package shop.chaekmate.core.cart.exception;

import static shop.chaekmate.core.cart.exception.CartErrorCode.CART_ITEM_NOT_FOUND;

import shop.chaekmate.core.common.exception.CoreException;

public class CartItemNotFoundException extends CoreException {
    public CartItemNotFoundException() {
        super(CART_ITEM_NOT_FOUND);
    }
}
