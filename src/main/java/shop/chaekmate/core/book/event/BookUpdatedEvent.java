package shop.chaekmate.core.book.event;

import shop.chaekmate.core.book.entity.Book;

public record BookUpdatedEvent(
        Book book
) {
}
