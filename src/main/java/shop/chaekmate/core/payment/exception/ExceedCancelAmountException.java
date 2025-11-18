package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class ExceedCancelAmountException extends CoreException {
    public ExceedCancelAmountException() {
        super(PaymentErrorCode.EXCEED_CANCEL_AMOUNT);
    }
}
