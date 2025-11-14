package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class NotFoundPaymentMethodException extends CoreException {
    public NotFoundPaymentMethodException() {
        super(PaymentErrorCode.NOT_FOUND_PAYMENT);
    }
}
