package shop.chaekmate.core.payment.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum PaymentErrorCode implements BaseErrorCode {

    NOT_FOUND_PAYMENT_METHOD(HttpStatus.NOT_FOUND, "PAYMENT-404-1", "해당 결제방식이 존재하지 않습니다."),
    NOT_FOUND_ORDER_NUMBER(HttpStatus.NOT_FOUND, "PAYMENT-404-2", "해당 주문번호가 존재하지 않습니다."),
    NOT_FOUND_PAYMENT_KEY(HttpStatus.NOT_FOUND, "PAYMENT-404-3", "해당 키가 존재하지 않습니다."),
    ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "PAYMENT-400-1", "이미 취소된 결제입니다."),
    INVALID_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "PAYMENT-400-2", "취소 금액은 0보다 커야 합니다."),
    EXCEED_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "PAYMENT-400-3", "취소 금액이 결제 가능 금액(현금 또는 포인트)을 초과할 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    PaymentErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
