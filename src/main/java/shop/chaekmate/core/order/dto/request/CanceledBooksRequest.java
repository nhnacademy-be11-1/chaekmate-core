package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Schema(description = "취소할 책 목록 요청")
public record CanceledBooksRequest(

        @Schema(description = "책 식별자")
        @NotBlank(message = "필수 값입니다.")
        long orderedBookId,

        @Schema(description = "책 수량")
        @NotBlank(message = "필수 값입니다.")
        @Positive(message = "취소할 책은 1개 이상이어야 합니다.")
        int canceledQuantity
) { }