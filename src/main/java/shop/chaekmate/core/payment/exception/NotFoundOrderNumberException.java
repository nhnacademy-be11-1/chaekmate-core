package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class NotFoundOrderNumberException extends CoreException {
    public NotFoundOrderNumberException() {
        super(PaymentErrorCode.NOT_FOUND_ORDER_NUMBER);
    }
}
