package shop.chaekmate.core.point.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatePointPolicyException extends CoreException {
    public DuplicatePointPolicyException() {
        super(PointErrorCode.DUPLICATED_POLICY);
    }
}
