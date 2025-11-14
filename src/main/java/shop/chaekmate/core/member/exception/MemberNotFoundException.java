package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {
    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}
