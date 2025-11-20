package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원 등급 히스토리 생성 요청")
public record CreateMemberGradeHistoryRequest(
        Long memberId,
        String gradeName,
        String reason
) {
}
