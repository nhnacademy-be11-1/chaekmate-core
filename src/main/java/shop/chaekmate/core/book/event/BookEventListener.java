package shop.chaekmate.core.book.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
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

    // 책 등록
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookCreated(BookCreatedEvent event){
        try {
            EventType eventType = EventType.INSERT;
            sendInsertOrUpdate(eventType, event.book());
        } catch (Exception e){
            log.error("Failed to send MQ message for bookId={},bookName={}", event.book().getId(),event.book().getTitle(), e);
        }
    }

    // 책 수정
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookUpdated(BookUpdatedEvent event){
        try {
            EventType eventType = EventType.UPDATE;
            sendInsertOrUpdate(eventType, event.book());
        } catch (Exception e){
            log.error("Failed to send MQ message for bookId={},bookName={}", event.book().getId(),event.book().getTitle(), e);
        }
    }

    // 책 삭제
    @Async
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

    // 책 섬네일 등록,수정
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBookThumbnail(BookThumbnailEvent event){
        try {
            EventType eventType = EventType.UPDATE;
            BookInfoMqRequest bookInfoMqRequest = BookInfoMqRequest.ofBookObjects(event.bookId(), event.thumbnailUrl(),null,null,null);
            bookMessagePublisher.sendBookTaskMessage(eventType, bookInfoMqRequest);
        } catch (Exception e){
            log.error("Failed to send MQ UPDATE message for bookId={}, thumbnailUrl={}", event.bookId(), event.thumbnailUrl());
        }
    }

    // 책 리뷰 요약 생성, 변경
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewSummaryUpdated(BookReviewSummaryEvent event) {
        try {
            EventType eventType = EventType.UPDATE;

            BookInfoMqRequest msg = BookInfoMqRequest.ofBookObjects(event.bookId(), null, event.reviewSummary(), null,null);

            bookMessagePublisher.sendBookTaskMessage(eventType, msg);

        } catch (Exception e) {
            log.error("Failed MQ send: review summary change bookId={}", event.bookId(), e);
        }
    }

    // 책 평점 생성, 변경
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleRatingUpdated(BookRatingEvent event) {
        try {
            EventType eventType = EventType.UPDATE;

            BookInfoMqRequest msg = BookInfoMqRequest.ofBookObjects(event.bookId(), null, null,event.rating(),null);

            bookMessagePublisher.sendBookTaskMessage(eventType, msg);

        } catch (Exception e) {
            log.error("Failed MQ send: rating change bookId={}", event.bookId(), e);
        }
    }

    // 책 리뷰 카운트 생성, 변경 (집계시)
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewCountUpdated(BookReviewCountEvent event) {
        try {
            EventType eventType = EventType.UPDATE;

            BookInfoMqRequest msg = BookInfoMqRequest.ofBookObjects(event.bookId(), null, null, null, event.reviewCount());

            bookMessagePublisher.sendBookTaskMessage(eventType, msg);

        } catch (Exception e) {
            log.error("Failed MQ send: reviewCnt change bookId={}", event.bookId(), e);
        }
    }

    private void sendInsertOrUpdate(EventType eventType, Book book) {
        Long bookId = book.getId();
        List<String> categories = bookCategoryRepository.findCategoryNamesByBookId(bookId);
        List<String> tags = bookTagRepository.findTagNamesByBookId(bookId);

        BookInfoMqRequest bookInfoMqRequest = BookInfoMqRequest.of(book, categories, tags);
        bookMessagePublisher.sendBookTaskMessage(eventType, bookInfoMqRequest);
    }
}
