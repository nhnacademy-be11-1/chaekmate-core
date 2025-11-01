package shop.chaekmate.core.book.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum TagErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "TAG-404", "해당하는 Id 의 Tag 를 찾을 수 없습니다."),
    DUPLICATE_NAME(HttpStatus.CONFLICT, "TAG-409", " 이미 존재하는 Tag 입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    TagErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
