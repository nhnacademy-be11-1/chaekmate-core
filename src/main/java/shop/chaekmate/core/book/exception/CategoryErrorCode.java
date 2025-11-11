package shop.chaekmate.core.book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum CategoryErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "해당 카테고리를 찾을 수 없습니다."),
    PARENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY-404", "해당 부모 카테고리를 찾을 수 없습니다."),
    CHILD_EXISTS(HttpStatus.BAD_REQUEST, "CATEGORY-400", "하위 카테고리가 존재하여 삭제할 수 없습니다."),
    BOOK_EXISTS(HttpStatus.BAD_REQUEST, "CATEGORY-400", "카테고리에 책이 존재하여 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
