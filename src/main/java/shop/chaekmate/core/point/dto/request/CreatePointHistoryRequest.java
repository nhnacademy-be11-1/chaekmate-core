package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreatePointHistoryRequest (
        @NotNull(message = "포인트는 필수입니다.")
        @Positive(message = "포인트는 양수여야 합니다.")
        int point,
        @NotBlank(message = "포인트 출처는 필수입니다.")
        String source
){
}
