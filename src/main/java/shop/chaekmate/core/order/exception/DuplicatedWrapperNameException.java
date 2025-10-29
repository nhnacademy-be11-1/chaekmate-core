package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatedWrapperNameException extends CoreException {
    public DuplicatedWrapperNameException() {
        super(WrapperErrorCode.DUPLICATED_NAME);
    }
}
