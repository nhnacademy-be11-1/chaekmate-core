package shop.chaekmate.core.point.dto.response;

import java.time.LocalDateTime;
import shop.chaekmate.core.point.entity.type.PointSpendType;

public record PointHistoryResponse(
        Long id,
        Long member,
        PointSpendType type,
        int point,
        String source,
        LocalDateTime createdAt
) {
}
