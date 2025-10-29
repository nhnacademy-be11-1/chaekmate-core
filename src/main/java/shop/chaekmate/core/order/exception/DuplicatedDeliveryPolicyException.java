package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatedDeliveryPolicyException extends CoreException {
    public DuplicatedDeliveryPolicyException() {
        super(DeliveryPolicyErrorCode.DUPLICATED_POLICY);
    }
}
