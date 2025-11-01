package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "태그 수정 응답")
public record UpdateTagResponse(
        @Schema(description = "태그 ID", example = "12")
        Long id,

        @Schema(description = "태그명", example = "IT")
        String name
) {
}
