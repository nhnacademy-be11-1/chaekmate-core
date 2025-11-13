package shop.chaekmate.core.cart.exception.cart;

import shop.chaekmate.core.common.exception.CoreException;

public class MemberNotFoundException extends CoreException {
    public MemberNotFoundException() {
        super(CartErrorCode.MEMBER_NOT_FOUND);
    }
}
