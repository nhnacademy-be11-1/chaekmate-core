package shop.chaekmate.core.payment.dto.response.base;

import java.time.OffsetDateTime;

public interface PaymentApproveResponseBase extends PaymentResponse {

    String orderNumber();

    long totalAmount();

    int pointUsed();

    String status();

    OffsetDateTime approvedAt();
}