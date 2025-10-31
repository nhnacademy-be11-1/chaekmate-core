package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdatePointValueRequest(

        @NotNull(message = "포인트 값은 필수입니다")
        @Min(value = 0, message = "포인트 값은 0 이상이어야 합니다")
        Integer number) {
}

