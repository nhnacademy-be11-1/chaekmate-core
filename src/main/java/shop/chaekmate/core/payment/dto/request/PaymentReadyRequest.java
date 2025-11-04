package shop.chaekmate.core.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import shop.chaekmate.core.payment.entity.type.PaymentMethod;

@Schema(description = "결제 요청")
public record PaymentReadyRequest(

        @Schema(description = "주문 번호", example = "ORD-20251103-000123")
        @NotBlank(message = "주문 번호는 필수 입력 값입니다.")
        String orderNumber,

        @Schema(description = "결제 금액", example = "29800")
        @Positive(message = "결제 금액은 0보다 커야 합니다.")
        long amount,

        @Schema(description = "결제 수단", example = "TOSS")
        @NotNull(message = "결제 수단은 반드시 선택해야 합니다.")
        PaymentMethod paymentMethod
) {}
