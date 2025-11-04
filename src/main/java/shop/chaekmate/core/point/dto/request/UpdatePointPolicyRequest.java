package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record UpdatePointPolicyRequest(
        @NotNull(message = "적용할 정책 타입은 필수입니다.")
        PointEarnedType type,
        @NotNull(message = "포인트 값은 필수입니다.")
        @PositiveOrZero
        Integer point
) {
}
