package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class AddressLimitExceededException extends CoreException {
    public AddressLimitExceededException() {
        super(MemberErrorCode.ADDRESS_LIMIT_EXCEEDED);
    }
}
