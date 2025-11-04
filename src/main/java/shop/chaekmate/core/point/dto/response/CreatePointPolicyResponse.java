package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record CreatePointPolicyResponse (
        Long id,
        PointEarnedType pointEarnedType,
        int point
){ }
