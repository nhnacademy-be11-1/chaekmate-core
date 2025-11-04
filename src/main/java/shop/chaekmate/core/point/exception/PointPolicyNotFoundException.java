package shop.chaekmate.core.point.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class PointPolicyNotFoundException extends CoreException {
    public PointPolicyNotFoundException() {
        super(PointPolicyErrorCode.NOT_FOUND);
    }
}
