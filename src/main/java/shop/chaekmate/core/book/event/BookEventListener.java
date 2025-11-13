package shop.chaekmate.core.book.event;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.book.dto.rabbit.BookMqRequest;
import shop.chaekmate.core.book.dto.rabbit.EventType;
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
            Long bookId = event.book().getId();
            List<String> categories = bookCategoryRepository.findCategoryNamesByBookId(bookId);
            List<String> tags = bookTagRepository.findTagNamesByBookId(bookId);

            BookMqRequest bookMqRequest = BookMqRequest.of(event.book(), null, categories, tags);
            bookMessagePublisher.sendBookRegisterMessage(eventType, bookMqRequest);
        } catch (Exception e){
            log.error("Failed to send MQ message for bookId={},bookName={}", event.book().getId(),event.book().getTitle(), e);
        }
    }
}
