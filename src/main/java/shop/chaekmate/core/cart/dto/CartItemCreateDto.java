package shop.chaekmate.core.cart.dto;

public record CartItemCreateDto (
        Long memberId,
        String guestId,
        Long bookId,
        int quantity
) implements CartOwner {
}
