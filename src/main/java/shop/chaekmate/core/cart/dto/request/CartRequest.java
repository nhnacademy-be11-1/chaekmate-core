package shop.chaekmate.core.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "장바구니 요청")
public record CartRequest(

        @NotNull
        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
}
