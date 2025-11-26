package shop.chaekmate.core.payment.dto.response.impl;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import shop.chaekmate.core.payment.dto.response.base.PaymentAbortedResponseBase;

@Schema(description = "결제 실패 응답")
public record PaymentAbortedResponse(

        @Schema(description = "에러 코드")
        String code,

        @Schema(description = "에러 메시지")
        String message,

        @Schema(description = "결제 실패 시각")
        LocalDateTime abortedAt

) implements PaymentAbortedResponseBase {}
