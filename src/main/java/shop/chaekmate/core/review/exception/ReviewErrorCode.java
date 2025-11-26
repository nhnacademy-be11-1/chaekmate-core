package shop.chaekmate.core.review.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum ReviewErrorCode implements BaseErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER-404", "회원이 존재하지 않습니다."),
    ORDERED_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDERED-BOOK-404", "주문된 도서를 찾을 수 없습니다"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-404", "리뷰를 찾을 수 없습니다."),
    REVIEW_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW-IMAGE-404", "해당 리뷰 이미지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
