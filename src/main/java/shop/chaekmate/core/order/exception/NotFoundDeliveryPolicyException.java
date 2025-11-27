package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class NotFoundDeliveryPolicyException extends CoreException {

    public NotFoundDeliveryPolicyException() {
        super(DeliveryPolicyErrorCode.NOT_FOUND);
    }
}
