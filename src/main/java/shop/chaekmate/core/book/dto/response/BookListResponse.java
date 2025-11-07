package shop.chaekmate.core.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BookListResponse(
        Long id,
        String title,
        String author,
        String publisher,
        int salesPrice
) {
    @QueryProjection
    public BookListResponse {}
}
