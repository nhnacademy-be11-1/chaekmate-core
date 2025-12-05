package shop.chaekmate.core.payment.event;

import shop.chaekmate.core.order.dto.response.ReturnBooksResponse;

public record ReturnRequestedEvent(ReturnBooksResponse response) {}
