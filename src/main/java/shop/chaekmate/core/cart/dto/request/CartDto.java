package shop.chaekmate.core.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "장바구니 DTO")
public record CartDto(

        @Schema(description = "회원 ID", example = "1")
        Long memberId
) {
}
