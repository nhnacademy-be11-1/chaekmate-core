package shop.chaekmate.core.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "장바구니 아이템 요청")
public record CartItemRequest(

        @NotNull
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "장바구니 ID", example = "1")
        @NotNull
        Long cartId,

        @NotNull
        @Schema(description = "도서 ID", example = "1")
        Long bookId,

        @Positive
        @Schema(description = "수량", example = "1")
        int quantity
) {
}
