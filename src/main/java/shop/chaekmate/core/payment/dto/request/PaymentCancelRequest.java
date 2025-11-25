package shop.chaekmate.core.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import java.util.List;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;

@Schema(description = "결제 취소 요청")
public record PaymentCancelRequest(
        // 결제 사 연동 시 필요(포인트 null)
        @Schema(description = "결제 키 (결제사에서 전달받은 고유 키, 포인트 결제 시 null)")
        String paymentKey,

        @Schema(description = "주문 번호")
        @NotBlank(message = "주문 번호는 필수 입력 값입니다.")
        @JsonProperty("orderId")
        String orderNumber,

        @Schema(description = "취소 사유", example = "파손으로 인한 환불")
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason,

        @Schema(description = "취소 금액 (입력하지 않으면 전체 취소)", example = "29800")
        @Positive(message = "취소 금액은 0보다 커야 합니다.")
        long cancelAmount,

        @Schema(description = "취소할 도서 목록")
        @NotEmpty(message = "취소할 도서는 1개 이상이어야 합니다.")
        List<CanceledBooksRequest> canceledBooks
) {}
