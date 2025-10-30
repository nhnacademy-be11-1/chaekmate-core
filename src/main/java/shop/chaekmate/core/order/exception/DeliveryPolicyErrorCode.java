package shop.chaekmate.core.order.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum DeliveryPolicyErrorCode implements BaseErrorCode {

    INVALID_POLICY(HttpStatus.BAD_REQUEST, "DELIVERY_Policy-400", "유효하지 않은 배송 정책 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "DELIVERY_Policy-404", "활성화된 배송 정책이 존재하지 않습니다."),
    DUPLICATED_POLICY(HttpStatus.CONFLICT, "DELIVERY_Policy-409", "이미 동일한 배송 정책이 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    DeliveryPolicyErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
