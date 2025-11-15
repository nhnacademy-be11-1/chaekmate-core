package shop.chaekmate.core.book.dto.rabbit;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;

@Builder
public record BookInfoMqRequest(
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

    // Book 엔티티 생성,변경 시 사용 (카테고리, 태그는 Book 서비스에 포함)
    public static BookInfoMqRequest of(Book book, List<String> categories, List<String> tags){
        return BookInfoMqRequest.builder()
                .dtoType("BOOK_INFO")
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .price(book.getPrice())
                .description(book.getDescription())
                .categories(categories)
                .publicationDatetime(
                        book.getPublishedAt() == null ? LocalDate.now() : book.getPublishedAt().toLocalDate()
                )
                .isbn(book.getIsbn())
                .publisher(book.getPublisher())
                .tags(tags)
                .build();
    }


    // 책 부가정보 엔티티들 변경 이벤트 발생 시 사용
    public static BookInfoMqRequest ofBookObjects(Long bookId, String thumbnailUrl, String reviewSummary, Double rating, Integer reviewCnt){
        return BookInfoMqRequest.builder()
                .dtoType("BOOK_INFO")
                .id(bookId)
                .bookImages(thumbnailUrl)
                .reviewSummary(reviewSummary)
                .rating(rating)
                .reviewCnt(reviewCnt)
                .build();
    }

}
