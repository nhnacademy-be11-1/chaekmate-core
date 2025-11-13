package shop.chaekmate.core.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 아이템 DTO")
public record CartItemDto(
        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "장바구니 ID", example = "1")
        Long cartId,

        @Schema(description = "도서 ID", example = "1")
        Long bookId,

        @Schema(description = "수량", example = "1")
        int quantity
) {
}
