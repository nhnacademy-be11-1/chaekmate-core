package shop.chaekmate.core.book.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum CategoryErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "해당 ID의 카테고리를 찾을 수 없습니다"),
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "해당하는 ID의 부모 카테고리를 찾을 수 없습니다."),
    BOOK_EXISTS(HttpStatus.CONFLICT, "CATEGORY-409", "해당 카테고리에 해당하는 책이 있어 삭제가 불가능 합니다"),
    CHILD_EXISTS(HttpStatus.CONFLICT, "CATEGORY-409", "해당 카테고리의 하위 카테고리가 있어 삭제가 불가능 합니다");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CategoryErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
