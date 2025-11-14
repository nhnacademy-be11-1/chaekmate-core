package shop.chaekmate.core.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.OffsetDateTime;
import java.util.List;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;

@Schema(description = "결제 취소 응답")
public record PaymentCancelResponse(

        @Schema(description = "주문 번호")
        @JsonProperty("orderId")
        String orderNumber,

        @Schema(description = "취소 사유", example = "사용자 요청으로 인한 환불")
        String cancelReason,

        @Schema(description = "취소된 금액")
        long canceledAmount,

        @Schema(description = "취소 완료 시각")
        OffsetDateTime canceledAt,

        @Schema(description = "취소된 도서 목록")
        List<CanceledBooksRequest> canceledBooks
) {}

