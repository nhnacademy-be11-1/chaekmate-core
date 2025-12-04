package shop.chaekmate.core.order.dto.response;

import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.type.OrderStatusType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
public record OrderHistoryResponse(
        Long orderId,
        String orderNumber,
        LocalDateTime orderDate,
        String ordererName,
        String ordererPhone,
        String ordererEmail,
        String recipientName,
        String recipientPhone,
        String zipcode,
        String streetName,
        String detail,
        String deliveryRequest,
        LocalDate deliveryAt,
        int deliveryFee,
        long totalPrice,
        OrderStatusType status,
        LocalDateTime createdAt,
        List<OrderedBookHistoryResponse> orderedBooks
) {
    public static OrderHistoryResponse of(Order order, List<OrderedBookHistoryResponse> orderedBooks) {
        return new OrderHistoryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getOrdererName(),
                order.getOrdererPhone(),
                order.getOrdererEmail(),
                order.getRecipientName(),
                order.getRecipientPhone(),
                order.getZipcode(),
                order.getStreetName(),
                order.getDetail(),
                order.getDeliveryRequest(),
                order.getDeliveryAt(),
                order.getDeliveryFee(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getCreatedAt(),
                orderedBooks
        );
    }
}
