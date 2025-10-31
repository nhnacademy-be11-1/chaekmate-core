package shop.chaekmate.core.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum WrapperErrorCode implements BaseErrorCode {

    INVALID_PRICE(HttpStatus.BAD_REQUEST, "WRAPPER-400", "유효하지 않은 포장지 가격입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "WRAPPER-404", "해당 포장지를 찾을 수 없습니다."),
    DUPLICATED_NAME(HttpStatus.CONFLICT, "WRAPPER-409", "중복된 포장지 이름입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    WrapperErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
