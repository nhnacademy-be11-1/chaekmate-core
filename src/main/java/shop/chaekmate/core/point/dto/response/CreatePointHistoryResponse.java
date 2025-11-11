package shop.chaekmate.core.point.dto.response;

import shop.chaekmate.core.point.entity.type.PointSpendType;

public record CreatePointHistoryResponse (
        Long id,
        Long member,
        PointSpendType type,
        int point,
        String source
) {
}
