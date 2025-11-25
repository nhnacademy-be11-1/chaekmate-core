package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class BaseGradeNotAllowedException extends CoreException {
    public BaseGradeNotAllowedException() {
        super(MemberErrorCode.GRADE_BASE_PROTECTED);
    }
}
