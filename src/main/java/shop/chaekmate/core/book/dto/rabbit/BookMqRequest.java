package shop.chaekmate.core.book.dto.rabbit;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import shop.chaekmate.core.book.entity.Book;

@Builder
public record BookMqRequest (
    String dtoType,
    long id,
    String title,
    String author,
    Integer price,
    String description,
    String isbn,
    String publisher,
    String bookImages,
    List<String> categories,
    LocalDate publicationDatetime,
    List<String> tags,
    String reviewSummary,
    Double rating,
    Integer reviewCnt
){

    public static BookMqRequest of(Book book, String bookImages, List<String> categories, List<String> tags){
        return BookMqRequest.builder()
                .dtoType("BOOK_INFO")
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .description(book.getDescription())
                .bookImages(bookImages)
                .categories(categories)
                .publicationDatetime(
                        book.getPublishedAt() == null ? LocalDate.now() : book.getPublishedAt().toLocalDate()
                )
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .tags(tags)
                .build();
    }
}
