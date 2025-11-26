package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatedGradeStandardAmountException extends CoreException {
    public DuplicatedGradeStandardAmountException() {
        super(MemberErrorCode.DUPLICATED_GRADE_STANDARD_AMOUNT);
    }
}
