package shop.chaekmate.core.cart.dto;

public record CartItemDeleteAllDto(
        Long memberId,
        String guestId
) implements CartOwner {
}
