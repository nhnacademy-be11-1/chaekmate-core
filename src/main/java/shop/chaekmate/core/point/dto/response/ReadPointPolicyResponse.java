package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record ReadPointPolicyResponse(
        Long id,
        PointEarnedType earnType,
        int point
) {
}
