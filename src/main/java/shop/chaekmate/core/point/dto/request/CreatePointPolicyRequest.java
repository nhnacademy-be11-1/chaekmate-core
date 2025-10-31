package shop.chaekmate.core.point.dto.request;

import jakarta.validation.constraints.NotNull;
import org.aspectj.weaver.ast.Not;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

import java.awt.*;

public record CreatePointPolicyRequest (
        @NotNull(message = "생성할 정책 입력은 필수 입니다.")
        PointEarnedType earnedType,
        @NotNull(message = "생성할 포인트 값은 필수 입니다.")
        int point
){
}
