package shop.chaekmate.core.book.dto.response;

public record CreateCategoryResponse(Long id, Long parentCategoryId, String name) {
}
