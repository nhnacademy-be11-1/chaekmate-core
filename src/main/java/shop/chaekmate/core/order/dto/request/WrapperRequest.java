package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "포장지 등록/수정 요청 DTO")
public record WrapperRequest(
        @Schema(description = "포장지 이름", example = "포장지1")
        @NotBlank @Size(max = 30) String name,

        @Schema(description = "포장지 가격", example = "1000")
        @Min(0) int price
) {
}
