package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "도서 이미지 정보 응답 DTO")
@Builder
public record BookImageResponse(
        @Schema(description = "이미지 ID", example = "1")
        Long bookImageId,
        @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
        String imageUrl,
        @Schema(description = "썸네일 여부", example = "true")
        boolean isThumbnail
) {
}
