package shop.chaekmate.core.cart.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Schema(description = "장바구니 아이템 목록 응답")
public record CartItemListResponse(

        @Schema(description = "회원 ID", example = "1")
        Long memberId,

        @Schema(description = "장바구니 ID", example = "1")
        Long cartId,

        @Schema(description = "장바구니 아이템 목록")
        List<CartItemResponse> items
) {
}
