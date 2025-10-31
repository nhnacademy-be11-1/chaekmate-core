package shop.chaekmate.core.order.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포장지 응답")
public record WrapperResponse(

        @Schema(description = "포장지 ID", example = "1")
        Long id,

        @Schema(description = "포장지 이름", example = "포장지1")
        String name,

        @Schema(description = "포장지 가격", example = "1000")
        int price
) {
}
