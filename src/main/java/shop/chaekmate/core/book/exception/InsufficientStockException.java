package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class InsufficientStockException extends CoreException {
    public InsufficientStockException() {
        super(BookErrorCode.BOOK_STOCK_SHORTAGE);
    }
}
