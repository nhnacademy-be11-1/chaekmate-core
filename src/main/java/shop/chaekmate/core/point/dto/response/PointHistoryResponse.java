package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.point.entity.type.PointSpendType;

public record PointHistoryResponse(
        Long id,
        Member member,
        PointSpendType type,
        int point,
        String source
) {
}
