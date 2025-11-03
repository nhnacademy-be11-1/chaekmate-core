package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "카테고리 페이지네이션 응답")
public record CategoryHierarchyResponse(
        @Schema(description = "카테고리 ID", example = "17")
        Long id,
        @Schema(description = "카테고리 계층 구조", example = "국내도서 > 소설 > 현대소설")
        String hierarchy
) {
}
