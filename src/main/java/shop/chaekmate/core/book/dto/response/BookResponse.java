package shop.chaekmate.core.book.dto.response;

import shop.chaekmate.core.book.entity.Book;

import java.time.LocalDateTime;
import java.util.List;

public record BookResponse(
        Long id,
        String title,
        String index,
        String description,
        String author,
        String publisher,
        LocalDateTime publishedAt,
        String isbn,
        Integer price,
        Integer salesPrice,
        Boolean isWrappable,
        Boolean isSaleEnd,
        Integer stock,
        Long views,
        List<Long> categoryIds,
        List<Long> tagIds
) {
    public static BookResponse from(Book book, List<Long> categoryIds, List<Long> tagIds, Long totalViews) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getIndex(),
                book.getDescription(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedAt(),
                book.getIsbn(),
                book.getPrice(),
                book.getSalesPrice(),
                book.isWrappable(),
                book.isSaleEnd(),
                book.getStock(),
                totalViews,
                categoryIds,
                tagIds
        );
    }
}
