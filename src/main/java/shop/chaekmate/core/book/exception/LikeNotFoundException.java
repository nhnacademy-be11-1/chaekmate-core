package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class LikeNotFoundException extends CoreException {

    public LikeNotFoundException() {
        super(LikeErrorCode.NOT_FOUND);
    }
}
