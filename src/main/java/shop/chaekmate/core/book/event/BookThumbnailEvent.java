package shop.chaekmate.core.book.event;

public record BookThumbnailEvent(
        Long bookId,
        String thumbnailUrl
) {
}
