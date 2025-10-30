package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Schema(description = "포장지 등록/수정 요청")
public record WrapperRequest(

        @Schema(description = "포장지 이름", example = "포장지1")
        @NotBlank(message = "포장지 이름은 공백일 수 없습니다.") @Size(max = 30, message = "포장지 이름의 최대 길이는 30이하 입니다.")
        String name,

        @Schema(description = "포장지 가격", example = "1000")
        @PositiveOrZero(message = "가격은 0보다 작을 수 없습니다.")
        int price
) {
}
