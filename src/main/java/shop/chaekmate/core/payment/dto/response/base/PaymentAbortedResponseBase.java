package shop.chaekmate.core.payment.dto.response.base;

import java.time.LocalDateTime;

public interface PaymentAbortedResponseBase extends PaymentResponse {

    String code();

    String message();

    LocalDateTime approvedAt();
}