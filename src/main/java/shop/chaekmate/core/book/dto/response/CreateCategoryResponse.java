package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 생성 응답")
public record CreateCategoryResponse(
        @Schema(description = "카테고리 ID", example = "17")
        Long id,

        @Schema(description = "부모 카테고리 ID", example = "13")
        Long parentCategoryId,

        @Schema(description = "카테고리명", example = "소설")
        String name
) {
}
