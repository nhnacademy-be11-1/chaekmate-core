package shop.chaekmate.core.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "카테고리 생성 요청")
public record CreateCategoryRequest(
        @Schema(description = "부모 카테고리 ID")
        Long parentCategoryId,

        @Size(max = 255, message = "카테고리명은 255자 이하여야 합니다.")
        @Schema(description = "카테고리명", example = "소설")
        @NotBlank(message = "카테고리명은 필수입니다.")
        String name
) {
}
