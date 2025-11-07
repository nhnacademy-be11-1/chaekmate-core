package shop.chaekmate.core.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Schema(description = "결제 실패 응답 DTO")
public record PaymentFailResponse(

//        @JsonProperty("orderId")
        @Schema(description = "주문 번호", example = "test-V1StGXR8_Z5jdHi6B")
        String orderId,

        @Schema(description = "실패 사유", example = "잔액 부족으로 인한 실패")
        String message,

        @Schema(description = "실패된 금액", example = "29800")
        long canceledAmount,

        @Schema(description = "결제 상태", example = "FAILED")
        String status,

        @Schema(description = "결제 실패 시각", example = "2025-11-03T22:35:40")
        OffsetDateTime failedAt
) {}

/*
Path 파라미터
paymentKey 필수 · string
결제의 키값입니다. 최대 길이는 200자입니다. 결제를 식별하는 역할로, 중복되지 않는 고유한 값입니다.

Request Body 파라미터
cancelReason 필수 · string
결제를 취소하는 이유입니다. 최대 길이는 200자입니다.
        cancelAmount
취소할 금액입니다. 값이 없으면 전액 취소됩니다.
*/