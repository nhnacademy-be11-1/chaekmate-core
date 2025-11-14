package shop.chaekmate.core.payment.dto;

import java.time.OffsetDateTime;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

public record PaymentHistoryDto(
        String orderNumber,
        PaymentMethodType paymentType,
        PaymentStatusType paymentStatus,
        long totalAmount,
        String reason,
        OffsetDateTime occurredAt
) {
    public static PaymentHistoryDto from(PaymentHistory history) {
        return new PaymentHistoryDto(
                history.getPayment().getOrderNumber(),
                history.getPayment().getPaymentType(),
                history.getPaymentStatus(),
                history.getTotalAmount(),
                history.getReason(),
                history.getOccurredAt()
        );
    }
}
