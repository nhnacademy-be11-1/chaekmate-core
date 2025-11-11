package shop.chaekmate.core.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {

    INVALID_PAYMENT(HttpStatus.BAD_REQUEST, "PAYMENT-400", "유효하지 않은 결제 요청입니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "PAYMENT-404", "해당 결제방식이 존재하지 않습니다."),
    NOT_FOUND_ORDER_NUMBER(HttpStatus.NOT_FOUND, "PAYMENT-404", "해당 주문번호가 존재하지 않습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
