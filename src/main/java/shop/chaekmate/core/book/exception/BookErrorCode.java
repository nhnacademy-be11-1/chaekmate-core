package shop.chaekmate.core.book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum BookErrorCode implements BaseErrorCode {
    BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK-404", "해당 도서를 찾을 수 없습니다."),
    BOOK_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK-404", "해당 도서 이미지를 찾을 수 없습니다."),
    INVALID_SEARCH_CONDITION(HttpStatus.BAD_REQUEST, "BOOK-400", "검색 조건이 유효하지 않습니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOK-404", "해당 관리자를 찾을 수 없습니다."),
    BOOK_STOCK_SHORTAGE(HttpStatus.BAD_REQUEST, "BOOK-400", "도서 재고가 부족합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
