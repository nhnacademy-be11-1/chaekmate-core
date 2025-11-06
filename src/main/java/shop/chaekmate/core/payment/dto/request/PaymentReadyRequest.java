package shop.chaekmate.core.payment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import shop.chaekmate.core.payment.entity.type.PaymentType;

@Schema(description = "결제 준비 요청")
public record PaymentReadyRequest(

        @Schema(description = "결제 수단", example = "TOSS")
        @NotBlank(message = "결제 수단 선택은 필수 값입니다.")
        PaymentType paymentType,

        @Schema(description = "승인 금액", example = "29800")
        @Positive(message = "승인 금액은 0보다 커야 합니다.")
        long amount
) {}
