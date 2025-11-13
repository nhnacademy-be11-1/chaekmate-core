package shop.chaekmate.core.cart.exception.cartitem;

import shop.chaekmate.core.common.exception.CoreException;

public class BookInsufficientStockException extends CoreException {
    public BookInsufficientStockException() {
        super(CartItemErrorCode.BOOK_INSUFFICIENT_STOCK);
    }
}
