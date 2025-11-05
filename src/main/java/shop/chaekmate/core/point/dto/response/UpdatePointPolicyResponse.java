package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record UpdatePointPolicyResponse (
        Long id,
        PointEarnedType earnedType,
        int point
){ }
