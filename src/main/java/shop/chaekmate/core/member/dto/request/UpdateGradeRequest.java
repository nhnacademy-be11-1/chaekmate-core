package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "등급 수정 요청")
public record UpdateGradeRequest(
        String name,
        Byte pointRate,
        int upgradeStandardAmount
) {
}
