package shop.chaekmate.core.book.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
@AllArgsConstructor
public enum TagErrorCode implements BaseErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "TAG-404", "해당 태그를 찾을 수 없습니다."),
    DUPLICATE_NAME(HttpStatus.BAD_REQUEST, "TAG-400", "이미 존재하는 태그 이름입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
