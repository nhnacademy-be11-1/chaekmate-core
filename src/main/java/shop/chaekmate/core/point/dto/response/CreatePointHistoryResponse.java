package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public record CreatePointHistoryResponse (
    Long id,
    Member member,
    PointEarnedType type,
    int point,
    String source
) {
}
