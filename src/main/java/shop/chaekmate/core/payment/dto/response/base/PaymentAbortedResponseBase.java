package shop.chaekmate.core.payment.dto.response.base;

import java.time.OffsetDateTime;

public interface PaymentAbortedResponseBase extends PaymentResponse {

    String code();

    String message();

    OffsetDateTime approvedAt();
}