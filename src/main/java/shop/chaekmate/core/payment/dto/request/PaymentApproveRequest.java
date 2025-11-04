package shop.chaekmate.core.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "결제 승인 요청")
public record PaymentApproveRequest(

        @Schema(description = "결제 키 (결제사에서 전달받은 고유 키)", example = "test_sk_zXLkKEypNArWmo50nX3lmeaxYG5R")
        @NotBlank(message = "결제 키는 필수 값입니다.")
        String paymentKey,

        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        @NotBlank(message = "주문 번호는 필수 입력 값입니다.")
        String orderNumber,

        @Schema(description = "승인 금액", example = "29800")
        @Positive(message = "승인 금액은 0보다 커야 합니다.")
        long amount
) {}
