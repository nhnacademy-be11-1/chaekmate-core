package shop.chaekmate.core.cart.dto;

public record CartItemReadDto(
        Long memberId,
        String guestId
) implements CartOwner {
}
