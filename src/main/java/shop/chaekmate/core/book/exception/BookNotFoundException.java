package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class BookNotFoundException extends CoreException {
    public BookNotFoundException() {
        super(BookErrorCode.BOOK_NOT_FOUND);
    }
}
