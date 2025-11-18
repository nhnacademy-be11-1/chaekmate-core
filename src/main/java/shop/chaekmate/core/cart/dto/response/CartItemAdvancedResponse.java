package shop.chaekmate.core.cart.dto.response;

public record CartItemAdvancedResponse(
        Long bookId,
        String bookImageUrl,
        String bookTitle,
        int bookSalesPrice,
        int quantity
) {
}
