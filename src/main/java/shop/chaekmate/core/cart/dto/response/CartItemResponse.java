package shop.chaekmate.core.cart.dto.response;

public record CartItemResponse(
        Long bookId,
        int quantity
) {
}
