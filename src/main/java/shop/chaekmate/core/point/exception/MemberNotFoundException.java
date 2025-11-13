package shop.chaekmate.core.point.exception;

import shop.chaekmate.core.book.exception.LikeErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {
    public MemberNotFoundException() {
        super(LikeErrorCode.MEMBER_NOT_FOUND);
    }
}
