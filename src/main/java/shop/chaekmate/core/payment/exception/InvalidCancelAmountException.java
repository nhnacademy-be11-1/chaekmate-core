package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class InvalidCancelAmountException extends CoreException {
    public InvalidCancelAmountException() {
        super(PaymentErrorCode.INVALID_CANCEL_AMOUNT);
    }
}
