package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.NotNull;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record CreatePointPolicyRequest (
        @NotNull(message = "생성할 정책 입력은 필수 입니다.")
        PointEarnedType earnedType,
        @NotNull(message = "생성할 포인트 값은 필수 입니다.")
        int point
){
}
