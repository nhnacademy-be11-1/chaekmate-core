package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class RefundBeforeDeliveredException extends CoreException {
    public RefundBeforeDeliveredException() {
        super(PaymentErrorCode.REFUND_BEFORE_DELIVERED);
    }
}
