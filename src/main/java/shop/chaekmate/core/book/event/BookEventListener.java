package shop.chaekmate.core.book.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.book.dto.rabbit.BookDeleteMqRequest;
import shop.chaekmate.core.book.dto.rabbit.BookInfoMqRequest;
import shop.chaekmate.core.book.dto.rabbit.EventType;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.BookTagRepository;
import shop.chaekmate.core.book.service.BookMessagePublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookEventListener {

    private final BookMessagePublisher bookMessagePublisher;
    private final BookCategoryRepository bookCategoryRepository;
    private final BookTagRepository bookTagRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookCreated(BookCreatedEvent event){
        try {
            EventType eventType = EventType.INSERT;
            sendInsertOrUpdate(eventType, event.book());
        } catch (Exception e){
            log.error("Failed to send MQ message for bookId={},bookName={}", event.book().getId(),event.book().getTitle(), e);
        }
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookUpdated(BookUpdatedEvent event){
        try {
            EventType eventType = EventType.UPDATE;
            sendInsertOrUpdate(eventType, event.book());
        } catch (Exception e){
            log.error("Failed to send MQ message for bookId={},bookName={}", event.book().getId(),event.book().getTitle(), e);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookDeleted(BookDeletedEvent event){
        try {
            EventType eventType = EventType.DELETE;
            BookDeleteMqRequest bookDeleteMqRequest = BookDeleteMqRequest.of(event.id());
            bookMessagePublisher.sendBookTaskMessage(eventType, bookDeleteMqRequest);
        } catch (Exception e){
            log.error("Failed to send MQ DELETE message for bookId={}", event.id(), e);
        }
    }

    private void sendInsertOrUpdate(EventType eventType, Book book) {
        Long bookId = book.getId();
        List<String> categories = bookCategoryRepository.findCategoryNamesByBookId(bookId);
        List<String> tags = bookTagRepository.findTagNamesByBookId(bookId);

        BookInfoMqRequest bookInfoMqRequest = BookInfoMqRequest.of(book, null, categories, tags);
        bookMessagePublisher.sendBookTaskMessage(eventType, bookInfoMqRequest);
    }
}
