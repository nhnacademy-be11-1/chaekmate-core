package shop.chaekmate.core.review.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class ReviewNotFoundException extends CoreException {
    public ReviewNotFoundException() {
        super(ReviewErrorCode.REVIEW_NOT_FOUND);
    }
}