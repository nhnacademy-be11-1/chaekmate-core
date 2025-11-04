package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicatedLoginIdException extends CoreException {
    public DuplicatedLoginIdException(                                                                                                          ) {
        super(MemberErrorCode.DUPLICATED_LOGIN_ID);
    }
}
