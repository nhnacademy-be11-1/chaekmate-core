package shop.chaekmate.core.book.dto.response;

import shop.chaekmate.core.book.entity.Book;

public record AdminBookResponse(
        Long id,
        String title,
        String author,
        String imageUrl
) {
    public static AdminBookResponse of(Book book, String imageUrl) {
        return new AdminBookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                imageUrl
        );
    }
}
