package shop.chaekmate.core.payment.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

@Schema(description = "결제 승인 요청")
public record PaymentApproveRequest(

        @Schema(description = "결제 수단")
        @NotNull(message = "결제 수단 선택은 필수 값입니다.")
        PaymentMethodType paymentType,

        @Schema(description = "결제 키 (결제사에서 전달받은 고유 키, 포인트 결제 시 null)")
        String paymentKey,

        @Schema(description = "주문 번호")
        @NotBlank(message = "주문 번호는 필수 입력 값입니다.")
        @JsonProperty("orderId")
        String orderNumber,

        @Schema(description = "승인 금액")
        @PositiveOrZero(message = "승인 금액은 음수가 될 수 없습니다.")
        long amount,

        @Schema(description = "포인트 사용 금액")
        @PositiveOrZero(message = "포인트는 음수가 될 수 없습니다.")
        int pointUsed
) {}
