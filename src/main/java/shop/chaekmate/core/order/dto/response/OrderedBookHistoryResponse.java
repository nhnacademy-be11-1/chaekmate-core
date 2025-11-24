package shop.chaekmate.core.order.dto.response;

import shop.chaekmate.core.order.entity.OrderedBook;

public record OrderedBookHistoryResponse(
        Long bookId,
        String bookTitle,
        int quantity,
        int finalUnitPrice
) {
    public static OrderedBookHistoryResponse from(OrderedBook orderedBook) {
        return new OrderedBookHistoryResponse(
                orderedBook.getBook().getId(),
                orderedBook.getBook().getTitle(),
                orderedBook.getQuantity(),
                orderedBook.getFinalUnitPrice()
        );
    }
}
