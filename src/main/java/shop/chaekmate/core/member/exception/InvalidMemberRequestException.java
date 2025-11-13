package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class InvalidMemberRequestException extends CoreException {
    public InvalidMemberRequestException() {
        super(MemberErrorCode.INVALID_MEMBER_REQUEST);
    }
}
