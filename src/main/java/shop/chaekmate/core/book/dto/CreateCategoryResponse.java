package shop.chaekmate.core.book.dto;

public record CreateCategoryResponse(Long id, Long parentCategoryId, String name) {
}
