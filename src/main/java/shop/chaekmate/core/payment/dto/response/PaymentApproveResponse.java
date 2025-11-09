package shop.chaekmate.core.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;

@Schema(description = "결제 승인 응답")
public record PaymentApproveResponse(

        @Schema(description = "주문 번호")
        @JsonProperty("orderId")
        String orderNumber,

        @Schema(description = "결제 키")
        String paymentKey,

        @Schema(description = "승인된 금액")
        long totalAmount,

        @Schema(description = "결제 상태", example = "DONE")
        String status,

        @Schema(description = "결제 승인 시각", example = "2025-11-03T22:32:22")
        OffsetDateTime approvedAt
) {}
