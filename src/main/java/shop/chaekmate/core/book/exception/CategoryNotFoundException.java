package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class CategoryNotFoundException extends CoreException {

    public CategoryNotFoundException() {
        super(CategoryErrorCode.NOT_FOUND);
    }
}
