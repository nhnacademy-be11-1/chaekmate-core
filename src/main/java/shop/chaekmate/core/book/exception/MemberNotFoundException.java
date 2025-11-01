package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {

    public MemberNotFoundException() {
        super(LikeErrorCode.MEMBER_NOT_FOUND);
    }
}
