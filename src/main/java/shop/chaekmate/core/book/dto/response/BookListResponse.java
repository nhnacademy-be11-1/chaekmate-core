package shop.chaekmate.core.book.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record BookListResponse(
        Long id,
        String title,
        String author,
        String publisher,
        int price,
        int salesPrice,
        String imageUrl
) {
    @QueryProjection
    public BookListResponse {
        // Querydsl에서 DTO를 안전하게 쿼리 결과로 매핑하기 위한 생성자
    }
}
