package shop.chaekmate.core.payment.dto.response.base;

import java.time.LocalDateTime;

public interface PaymentApproveResponseBase extends PaymentResponse {

    String orderNumber();

    long totalAmount();

    int pointUsed();

    String status();

    LocalDateTime approvedAt();
}