package shop.chaekmate.core.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "썸네일 수정 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ThumbnailUpdateRequest {

    @Schema(description = "새 썸네일 이미지 URL", example = "https://example.com/new-thumbnail.jpg")
    @NotBlank(message = "새 썸네일 이미지 URL은 필수입니다.")
    private String newImageUrl;
}
