package shop.chaekmate.core.book.dto;

public record UpdateCategoryRequest(Long id, Long parentCategoryId, String name) {
}

