package shop.chaekmate.core.cart.exception;

import static shop.chaekmate.core.cart.exception.CartErrorCode.CART_NOT_FOUND;

import shop.chaekmate.core.common.exception.CoreException;

public class CartNotFoundException extends CoreException {
    public CartNotFoundException() {
        super(CART_NOT_FOUND);
    }
}
