package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "배송 정책 등록 요청")
public record DeliveryPolicyRequest(

        @Schema(description = "무료 배송 기준 금액",example = "30000")
        @PositiveOrZero(message = "배송 기준 금액은 0보다 작을 수 없습니다.")
        int freeStandardAmount,

        @Schema(description = "배송비 금액", example = "5000")
        @PositiveOrZero(message = "배송비 금액은 0보다 작을 수 없습니다.")
        int deliveryFee
) {
}
