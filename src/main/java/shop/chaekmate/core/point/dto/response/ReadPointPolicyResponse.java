package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record ReadPointPolicyResponse(
        Long id,
        PointEarnedType earnType,
        int point
) {
    public static ReadPointPolicyResponse fromEntity(PointPolicy policy) {
        return new ReadPointPolicyResponse(policy.getId(), policy.getType(), policy.getPoint());
    }
}
