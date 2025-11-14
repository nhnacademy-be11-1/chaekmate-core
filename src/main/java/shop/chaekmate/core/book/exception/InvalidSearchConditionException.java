package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class InvalidSearchConditionException extends CoreException {
    public InvalidSearchConditionException() {
        super(BookErrorCode.INVALID_SEARCH_CONDITION);
    }
}
