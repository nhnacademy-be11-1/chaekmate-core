package shop.chaekmate.core.external.aladin.dto.response;

public record BookSearchResponse(
        String title,
        String author,
        String publisher,
        String publishedAt,
        String isbn,
        Integer price,
        Integer salesPrice,
        String coverImage,
        String description,
        String aladinCategoryName
) {
}
