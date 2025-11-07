package shop.chaekmate.core.book.exception.category;

import shop.chaekmate.core.book.exception.CategoryErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

public class CategoryNotFoundException extends CoreException {
    public CategoryNotFoundException() {
        super(CategoryErrorCode.NOT_FOUND);
    }
}
