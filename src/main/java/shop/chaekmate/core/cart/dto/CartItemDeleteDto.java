package shop.chaekmate.core.cart.dto;

public record CartItemDeleteDto(
        Long memberId,
        String guestId,
        Long bookId
) implements CartOwner {
}
