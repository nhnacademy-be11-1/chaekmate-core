package shop.chaekmate.core.cart.exception.cart;

import shop.chaekmate.core.common.exception.CoreException;

public class CartNotFoundException extends CoreException {
    public CartNotFoundException() {
        super(CartErrorCode.CART_NOT_FOUND);
    }
}
