package shop.chaekmate.core.order.event;

public record ShippingStartedEvent(String orderNumber, String bookTitle) {}
