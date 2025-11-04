package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class DuplicateTagNameException extends CoreException {

    public DuplicateTagNameException() {
        super(TagErrorCode.DUPLICATE_NAME);
    }
}
