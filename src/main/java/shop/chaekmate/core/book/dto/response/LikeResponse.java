package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 응답")
public record LikeResponse(
        @Schema(description = "좋아요 ID", example = "14")
        Long id,

        @Schema(description = "책 ID", example = "17765")
        Long bookId,

        @Schema(description = "회원 ID", example = "1234")
        Long memberId
) {
}
