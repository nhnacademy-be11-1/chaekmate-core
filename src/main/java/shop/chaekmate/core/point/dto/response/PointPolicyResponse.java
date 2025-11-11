package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record PointPolicyResponse(
        Long id,
        PointEarnedType earnType,
        int point
) {

}
