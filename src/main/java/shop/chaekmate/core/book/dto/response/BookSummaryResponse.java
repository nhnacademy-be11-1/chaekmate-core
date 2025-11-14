package shop.chaekmate.core.book.dto.response;

import shop.chaekmate.core.book.entity.Book;

public record BookSummaryResponse(
        Long id,
        String name
) {
    public static BookSummaryResponse from(Book book) {
        return new BookSummaryResponse(
                book.getId(),
                book.getTitle()
        );
    }
}
