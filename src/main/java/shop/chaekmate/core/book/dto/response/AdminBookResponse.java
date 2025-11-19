package shop.chaekmate.core.book.dto.response;

import lombok.Builder;
import shop.chaekmate.core.book.entity.Book;

@Builder
public record AdminBookResponse(
        Long id,
        String title,
        String author,
        String imageUrl, // thumbnail
        Integer reviewCount
) {
    public static AdminBookResponse of(Book book, String imageUrl,Integer reviewCount) {
        return AdminBookResponse.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .imageUrl(imageUrl)
                .reviewCount(reviewCount)
                .build();
    }
}
