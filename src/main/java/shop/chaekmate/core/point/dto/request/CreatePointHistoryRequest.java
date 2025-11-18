package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import shop.chaekmate.core.point.entity.type.PointSpendType;

public record CreatePointHistoryRequest (
        Long id,
        Long member,
        PointSpendType type,
        int point,
        String source
){
}
