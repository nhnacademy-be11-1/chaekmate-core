package shop.chaekmate.core.review.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class ReviewImageNotFoundException extends CoreException {
    public ReviewImageNotFoundException() {
        super(ReviewErrorCode.REVIEW_IMAGE_NOT_FOUND);
    }
}
