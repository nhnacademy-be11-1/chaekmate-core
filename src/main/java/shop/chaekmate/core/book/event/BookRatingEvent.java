package shop.chaekmate.core.book.event;

public record BookRatingEvent(
        Long bookId,
        Double rating
) {
}
