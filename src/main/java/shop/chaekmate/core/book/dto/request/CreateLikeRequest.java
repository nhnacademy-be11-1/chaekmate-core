package shop.chaekmate.core.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "좋아요 생성 요청")
public record CreateLikeRequest(
        @Schema(description = "회원 ID", example = "1234")
        @NotNull(message = "회원 ID는 필수입니다.")
        Long memberId
) {
}
