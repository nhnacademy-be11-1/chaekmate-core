package shop.chaekmate.core.payment.exception;

import lombok.Getter;

@Getter
public class PaymentApproveException extends RuntimeException {
    private final String code;

    public PaymentApproveException(String code, String message) {
        super(message);
        this.code = code;
    }
}
