package shop.chaekmate.core.cart.dto;

public record CartItemUpdateDto(
        Long memberId,
        String guestId,
        Long bookId,
        int quantity
) implements CartOwner, CartItem {
}
