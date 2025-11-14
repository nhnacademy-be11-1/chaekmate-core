package shop.chaekmate.core.payment.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class AlreadyCanceledException extends CoreException {
    public AlreadyCanceledException() {
        super(PaymentErrorCode.ALREADY_CANCELED);
    }
}
