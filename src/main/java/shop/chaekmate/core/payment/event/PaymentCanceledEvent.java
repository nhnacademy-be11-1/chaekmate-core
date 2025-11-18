package shop.chaekmate.core.payment.event;

import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;

public record PaymentCanceledEvent(PaymentCancelResponse cancelResponse) {}
