package shop.chaekmate.core.book.event;

import shop.chaekmate.core.book.entity.Book;

public record BookCreatedEvent(
        Book book
)
{
}
