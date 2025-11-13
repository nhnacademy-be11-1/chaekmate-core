package shop.chaekmate.core.payment.event;

import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;

public record PaymentApprovedEvent(PaymentApproveResponse approveResponse) {}
