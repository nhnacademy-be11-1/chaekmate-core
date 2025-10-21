package shop.chaekmate.core.book.dto;

public record CreateCategoryRequest(Long parentCategoryId, String name) {
}
