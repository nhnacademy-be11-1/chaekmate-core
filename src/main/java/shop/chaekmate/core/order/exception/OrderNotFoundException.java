package shop.chaekmate.core.order.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class OrderNotFoundException extends CoreException {

    public OrderNotFoundException() {
        super(OrderHistoryErrorCode.NOT_FOUND);
    }
}
