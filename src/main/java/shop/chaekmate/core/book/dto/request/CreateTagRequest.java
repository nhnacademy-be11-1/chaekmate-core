package shop.chaekmate.core.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "태그 생성 요청")
public record CreateTagRequest(
        @Schema(description = "태그명", example = "크리스마스")
        @NotBlank(message = "태그명은 필수입니다.")
        @Size(max = 255, message = "태그명은 255자 이하여야 합니다.")
        String name
) {
}
