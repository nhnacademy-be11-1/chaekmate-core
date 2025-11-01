package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class CategoryHasChildException extends CoreException {

    public CategoryHasChildException() {
        super(CategoryErrorCode.CHILD_EXISTS);
    }
}
