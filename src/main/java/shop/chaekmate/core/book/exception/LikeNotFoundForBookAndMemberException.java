package shop.chaekmate.core.book.exception;

import shop.chaekmate.core.common.exception.CoreException;

public class LikeNotFoundForBookAndMemberException extends CoreException {

    public LikeNotFoundForBookAndMemberException() {
        super(LikeErrorCode.LIKE_NOT_FOUND_FOR_BOOK_AND_MEMBER);
    }
}
