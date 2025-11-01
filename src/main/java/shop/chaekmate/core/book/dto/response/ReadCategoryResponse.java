package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "카테고리 조회 응답")
public record ReadCategoryResponse(
        @Schema(description = "카테고리 ID", example = "12")
        Long id,

        @Schema(description = "부모 카테고리 ID", example = "10")
        String parentCategoryId,

        @Schema(description = "카테고리명", example = "소설")
        String name
) {
}
