package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class NotFoundPaymentKeyException extends CoreException {
    public NotFoundPaymentKeyException() {
        super(PaymentErrorCode.NOT_FOUND_PAYMENT_KEY);
    }
}
