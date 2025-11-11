package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class BookImageNotFoundException extends CoreException {
    public BookImageNotFoundException() {
        super(BookErrorCode.BOOK_IMAGE_NOT_FOUND);
    }
}
