package shop.chaekmate.core.book.event;

public record BookReviewCountEvent(
        Long bookId,
        Integer reviewCount
) {
}
