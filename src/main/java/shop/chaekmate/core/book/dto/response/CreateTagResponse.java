package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 생성 응답")
public record CreateTagResponse(
        @Schema(description = "태그 ID", example = "1")
        Long id,

        @Schema(description = "태그명", example = "#IT")
        String name
) {
}
