package shop.chaekmate.core.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

@Schema(description = "외부 결제 승인 응답")
public record ApiApproveResponse(

        @Schema(description = "결제 수단")
        PaymentMethodType paymentType,

        @Schema(description = "결제 키 (결제사에서 전달받은 고유 키)")
        String paymentKey,

        @Schema(description = "주문 번호")
        @JsonProperty("orderId")
        String orderNumber,

        @Schema(description = "승인 금액")
        long amount
) {}
