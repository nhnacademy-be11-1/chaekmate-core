package shop.chaekmate.core.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "배달 정책 기록 응답")
public record DeliveryPolicyHistoryResponse(

        @Schema(description = "배송 정책 ID", example = "1")
        Long id,

        @Schema(description = "무료 배송 기준 금액", example = "30000")
        int freeStandardAmount,

        @Schema(description = "배송비 금액", example = "5000")
        int deliveryFee,

        @Schema(description = "정책 생성 일자", example = "2025-10-23T09:00:00")
        LocalDateTime createdAt,

        @Schema(description = "정책 종료 일자", example = "2025-10-24T09:00:00")
        LocalDateTime deletedAt
) {
}
