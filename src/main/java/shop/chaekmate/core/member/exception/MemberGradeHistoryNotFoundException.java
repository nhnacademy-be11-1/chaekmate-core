package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberGradeHistoryNotFoundException extends CoreException {
    public MemberGradeHistoryNotFoundException() {
        super(MemberErrorCode.MEMBER_HISTORY_NOT_FOUND);
    }
}
