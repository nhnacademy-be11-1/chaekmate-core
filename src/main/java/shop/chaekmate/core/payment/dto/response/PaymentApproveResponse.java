package shop.chaekmate.core.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "결제 승인 응답")
public record PaymentApproveResponse(

        @JsonProperty("orderId")
        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        String orderNumber,

//        @Schema(description = "결제 수단", example = "TOSS")
//        String method,

        @Schema(description = "결제 키", example = "test_sk_GjLJoQ1aVZbyBBQ2EYKPVw6KYe2R")
        String key,

        @Schema(description = "승인된 금액", example = "29800")
        long totalAmount,

        @Schema(description = "결제 상태", example = "DONE")
        String status,

        @Schema(description = "결제 승인 시각", example = "2025-11-03T22:32:22")
        LocalDateTime approvedAt
) {}
