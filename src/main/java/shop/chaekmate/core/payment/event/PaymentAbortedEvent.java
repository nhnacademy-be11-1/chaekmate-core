package shop.chaekmate.core.payment.event;

import shop.chaekmate.core.payment.dto.response.impl.PaymentAbortedResponse;

public record PaymentAbortedEvent(String orderNumber, PaymentAbortedResponse abortedResponse) {}

