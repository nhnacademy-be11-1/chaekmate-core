package shop.chaekmate.core.review.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class OrderedBookNotFoundException extends CoreException {
    public OrderedBookNotFoundException() {
        super(ReviewErrorCode.ORDERED_BOOK_NOT_FOUND);
    }
}
