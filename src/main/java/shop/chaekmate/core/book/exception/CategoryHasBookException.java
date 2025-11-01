package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class CategoryHasBookException extends CoreException {

    public CategoryHasBookException() {
        super(CategoryErrorCode.BOOK_EXISTS);
    }
}
