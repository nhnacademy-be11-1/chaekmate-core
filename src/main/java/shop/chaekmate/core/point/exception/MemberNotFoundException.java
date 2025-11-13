package shop.chaekmate.core.point.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {
    public MemberNotFoundException() {
        super(PointErrorCode.MEMBER_NOT_FOUND);
    }
}