package shop.chaekmate.core.cart.dto.response;

public record CartItemAdvancedResponse(
        Long bookId,
        String bookImageUrl,
        String bookTitle,
        int bookPrice,
        int bookSalesPrice,
        int quantity
) {
}
