package shop.chaekmate.core.cart.exception.cartitem;

import shop.chaekmate.core.common.exception.CoreException;

public class BookNotFoundException extends CoreException {
    public BookNotFoundException() {
        super(CartItemErrorCode.BOOK_NOT_FOUND);
    }
}
