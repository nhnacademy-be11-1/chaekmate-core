package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class ParentCategoryNotFoundException extends CoreException {

    public ParentCategoryNotFoundException() {
        super(CategoryErrorCode.PARENT_NOT_FOUND);
    }
}
