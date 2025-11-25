package shop.chaekmate.core.review.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {
    public MemberNotFoundException() {
        super(ReviewErrorCode.MEMBER_NOT_FOUND);
    }
}
