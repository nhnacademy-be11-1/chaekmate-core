package shop.chaekmate.core.book.exception.tag;

import shop.chaekmate.core.book.exception.TagErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

public class TagNotFoundException extends CoreException {
    public TagNotFoundException() {
        super(TagErrorCode.NOT_FOUND);
    }
}
