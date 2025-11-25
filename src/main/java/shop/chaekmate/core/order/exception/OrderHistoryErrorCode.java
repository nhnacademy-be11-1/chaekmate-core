package shop.chaekmate.core.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum OrderHistoryErrorCode implements BaseErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_HISTORY-404", "찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    OrderHistoryErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
