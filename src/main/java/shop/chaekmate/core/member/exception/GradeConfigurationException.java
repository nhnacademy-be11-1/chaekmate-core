package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class GradeConfigurationException extends CoreException{
    public GradeConfigurationException() {
        super(MemberErrorCode.GRADE_CONFIGURATION);
    }
}
