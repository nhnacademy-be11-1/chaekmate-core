package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;
import shop.chaekmate.core.payment.exception.PaymentErrorCode;

public class PaymentMethodNotFoundException extends CoreException {
    public PaymentMethodNotFoundException() {
        super(PaymentErrorCode.NOT_FOUND_PAYMENT);
    }
}
