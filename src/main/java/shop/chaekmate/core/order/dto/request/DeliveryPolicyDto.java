package shop.chaekmate.core.order.dto.request;

public record DeliveryPolicyDto(

        int freeStandardAmount,

        int deliveryFee
) {
}
