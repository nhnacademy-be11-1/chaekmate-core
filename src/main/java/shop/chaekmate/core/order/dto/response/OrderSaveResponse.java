package shop.chaekmate.core.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 저장 응답")
public record OrderSaveResponse(

        @Schema(description = "주문 번호", example = "q1w2e3r4t5y6")
        String orderNumber,

        @Schema(description = "결제 예정 총 금액", example = "29800")
        long totalPrice

) {
}
