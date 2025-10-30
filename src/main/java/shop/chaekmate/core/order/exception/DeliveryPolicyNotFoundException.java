package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DeliveryPolicyNotFoundException extends CoreException {

    public DeliveryPolicyNotFoundException() {
        super(DeliveryPolicyErrorCode.NOT_FOUND);
    }
}
