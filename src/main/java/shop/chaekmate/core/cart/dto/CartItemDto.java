package shop.chaekmate.core.cart.dto;

public record CartItemDto(
        String cartId,
        String ownerId,
        Long bookId,
        int quantity
) {
}
