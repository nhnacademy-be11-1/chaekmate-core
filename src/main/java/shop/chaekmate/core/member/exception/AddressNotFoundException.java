package shop.chaekmate.core.member.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class AddressNotFoundException extends CoreException {
    public AddressNotFoundException() {
        super(MemberErrorCode.ADDRESS_NOT_FOUND);
    }
}
