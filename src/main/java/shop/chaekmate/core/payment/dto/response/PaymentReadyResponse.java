package shop.chaekmate.core.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 준비 응답")
public record PaymentReadyResponse(

        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        String orderNumber,

        @Schema(description = "결제창 URL")
        String paymentUrl
) {}
