package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

/**
 * Unified update request: which policy (type) and the new point value.
 */
public record UpdatePointPolicyRequest(
        @NotNull(message = "적용할 정책 타입은 필수입니다.")
        PointEarnedType pointEarnedType,
        @NotNull(message = "포인트 값은 필수입니다.")
        @Min(value = 0, message = "포인트 값은 0 이상이어야 합니다")
        Integer point
) {
}
