package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class WrapperNotFoundException extends CoreException {
    public WrapperNotFoundException() {
        super(WrapperErrorCode.NOT_FOUND);
    }
}
