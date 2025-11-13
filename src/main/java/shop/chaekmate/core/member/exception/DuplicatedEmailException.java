package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.BaseErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatedEmailException extends CoreException {

    public DuplicatedEmailException() {
        super(MemberErrorCode.DUPLICATED_EMAIL);
    }
}
