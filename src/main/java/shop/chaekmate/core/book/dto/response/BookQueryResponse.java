package shop.chaekmate.core.book.dto.response;

public record BookQueryResponse(
        Long id,
        String title,
        String author,
        int price,
        int salesPrice,
        double rating,
        long reviewCount,
        String thumbnailUrl,
        long views
) {

}
