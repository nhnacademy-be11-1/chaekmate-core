package shop.chaekmate.core.cart.dto;

import java.util.List;

public record CartItemDeleteListDto(
        Long memberId,
        String guestId,
        List<CartItemDeleteSimpleDto> items
) implements CartOwner {
}
