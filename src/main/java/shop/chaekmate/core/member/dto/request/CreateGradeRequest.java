package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "등급 생성 요청")
public record CreateGradeRequest(
        String name,
        Byte pointRate,
        int upgradeStandardAmount
) {
}
