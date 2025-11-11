package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class AdminNotFoundException extends CoreException {
    public AdminNotFoundException() {
        super(BookErrorCode.ADMIN_NOT_FOUND);
    }
}
