package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record DeletePointPolicyResponse (
        Long id,
        PointEarnedType pointEarnedType,
        int point
){
}
