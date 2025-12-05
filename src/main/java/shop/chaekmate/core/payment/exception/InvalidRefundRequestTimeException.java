package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class InvalidRefundRequestTimeException extends CoreException {
    public InvalidRefundRequestTimeException() {
        super(PaymentErrorCode.INVALID_REFUND_REQUEST_TIME);
    }
}
