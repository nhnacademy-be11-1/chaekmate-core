package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class BookNotFoundException extends CoreException {

    public BookNotFoundException() {
        super(LikeErrorCode.BOOK_NOT_FOUND);
    }
}
