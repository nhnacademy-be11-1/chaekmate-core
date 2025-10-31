package shop.chaekmate.core.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "배달 정책 응답")
public record DeliveryPolicyResponse(

        @Schema(description = "배송 정책 ID", example = "1")
        Long id,

        @Schema(description = "무료 배송 기준 금액", example = "30000")
        int freeStandardAmount,

        @Schema(description = "배송비 금액", example = "5000")
        int deliveryFee
) {
}
