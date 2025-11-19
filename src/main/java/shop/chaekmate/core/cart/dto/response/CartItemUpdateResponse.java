package shop.chaekmate.core.cart.dto.response;

public record CartItemUpdateResponse(
        Long bookId,
        int quantity
) {
}
