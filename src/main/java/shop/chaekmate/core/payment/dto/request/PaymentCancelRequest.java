package shop.chaekmate.core.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "결제 취소 요청")
public record PaymentCancelRequest(

        @Schema(description = "결제 고유키 (Toss PaymentKey)", example = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R")
        @NotBlank(message = "결제 키는 필수 값입니다.")
        String paymentKey,

        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        @NotBlank(message = "주문 번호는 필수 입력 값입니다.")
        String orderNumber,

        @Schema(description = "취소 사유", example = "파손으로 인한 환불")
        @NotBlank(message = "취소 사유는 필수입니다.")
        String cancelReason,

        @Schema(description = "취소 금액 (입력하지 않으면 전체 취소)", example = "29800")
        @Positive(message = "취소 금액은 0보다 커야 합니다.")
        Long cancelAmount
) {}
