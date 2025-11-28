package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class NotFoundWrapperException extends CoreException {

    public NotFoundWrapperException() {
        super(WrapperErrorCode.NOT_FOUND);
    }
}
