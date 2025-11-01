package shop.chaekmate.core.book.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum LikeErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE-404", "해당하는 Id의 like 를 찾을 수 없습니다"),
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE-404", "해당하는 도서를 찾을 수 없습니다"),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE-404", "해당하는 회원을 찾을 수 없습니다"),
    LIKE_NOT_FOUND_FOR_BOOK_AND_MEMBER(HttpStatus.NOT_FOUND, "LIKE-404", "해당하는 bookId, memberId 의 like 가 없습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    LikeErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
