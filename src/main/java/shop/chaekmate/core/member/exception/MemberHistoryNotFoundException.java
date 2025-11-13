package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberHistoryNotFoundException extends CoreException {
    public MemberHistoryNotFoundException() {
        super(MemberErrorCode.MEMBER_HISTORY_NOT_FOUND);
    }
}
