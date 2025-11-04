package shop.chaekmate.core.book.dto.request;

public record BookSearchCondition(
        Long categoryId,
        Long tagId,
        String keyword
) {
}
