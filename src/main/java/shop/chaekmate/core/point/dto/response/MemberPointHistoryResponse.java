package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointSpendType;

import java.time.LocalDateTime;

public record MemberPointHistoryResponse(
        Long member,
        PointSpendType type,
        int point,
        String source,
        LocalDateTime createdAt
) {
}
