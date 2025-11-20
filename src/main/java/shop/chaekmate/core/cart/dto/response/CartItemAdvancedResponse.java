package shop.chaekmate.core.cart.dto.response;

public record CartItemAdvancedResponse(
        Long bookId,
        String bookImageUrl,
        String bookTitle,
        int bookPrice,          // 정가
        int bookSalesPrice,     // 판매가 (할인O)
        int stock,              // 재고
        int quantity
) {
}
