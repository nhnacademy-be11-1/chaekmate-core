package shop.chaekmate.core.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 아이템 응답")
public record CartItemResponse(

        @Schema(description = "도서 ID", example = "1")
        Long bookId,

        @Schema(description = "수량", example = "1")
        int quantity
) {
}
