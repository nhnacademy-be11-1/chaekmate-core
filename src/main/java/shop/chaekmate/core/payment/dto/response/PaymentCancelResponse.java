package shop.chaekmate.core.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 취소 응답 DTO")
public record PaymentCancelResponse(

        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        String orderNumber,

        @Schema(description = "취소 사유", example = "사용자 요청으로 인한 환불")
        String cancelReason,

        @Schema(description = "취소된 금액", example = "29800")
        long canceledAmount,

        @Schema(description = "결제 상태", example = "CANCELED")
        String status,

        @Schema(description = "취소 완료 시각", example = "2025-11-03T22:35:40")
        String canceledAt
) {}

