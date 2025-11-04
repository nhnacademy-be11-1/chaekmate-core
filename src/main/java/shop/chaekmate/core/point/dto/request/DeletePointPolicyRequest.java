package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.NotNull;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record DeletePointPolicyRequest (
        @NotNull(message = "삭제할 정책 입력은 필수 입니다. ")
        PointEarnedType pointEarnedType
){ }
