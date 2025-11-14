package shop.chaekmate.core.book.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "책 생성 응답")
public record BookCreateResponse(
        @Schema(description = "책 ID", example = "1")
        Long id
) {
}
