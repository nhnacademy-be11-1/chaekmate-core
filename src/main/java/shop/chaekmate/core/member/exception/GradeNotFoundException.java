package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class GradeNotFoundException extends CoreException {
    public GradeNotFoundException() {
        super(MemberErrorCode.GRADE_NOT_FOUND);
    }
}
