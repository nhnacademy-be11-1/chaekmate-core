package shop.chaekmate.core.cart.exception;

import static shop.chaekmate.core.cart.exception.CartErrorCode.BOOK_INSUFFICIENT_STOCK;

import shop.chaekmate.core.common.exception.CoreException;

public class BookInsufficientStockException extends CoreException {
    public BookInsufficientStockException() {
        super(BOOK_INSUFFICIENT_STOCK);
    }
}
