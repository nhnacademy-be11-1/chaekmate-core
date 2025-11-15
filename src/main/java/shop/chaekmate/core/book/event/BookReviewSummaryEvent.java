package shop.chaekmate.core.book.event;

public record BookReviewSummaryEvent(
        Long bookId,
        String reviewSummary
) {
}
