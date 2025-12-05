package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class RefundPeriodExceededException extends CoreException {
    public RefundPeriodExceededException() {
        super(PaymentErrorCode.REFUND_PERIOD_EXCEEDED);
    }
}
