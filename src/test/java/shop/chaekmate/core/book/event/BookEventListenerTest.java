package shop.chaekmate.core.book.event;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.core.book.dto.rabbit.BookDeleteMqRequest;
import shop.chaekmate.core.book.dto.rabbit.BookInfoMqRequest;
import shop.chaekmate.core.book.dto.rabbit.EventType;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookCategoryRepository;
import shop.chaekmate.core.book.repository.BookTagRepository;
import shop.chaekmate.core.book.service.BookMessagePublisher;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookEventListenerTest {

    @Mock
    private BookMessagePublisher publisher;

    @Mock
    private BookCategoryRepository categoryRepo;

    @Mock
    private BookTagRepository tagRepo;

    private BookEventListener listener;

    private Book book;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        listener = new BookEventListener(publisher, categoryRepo, tagRepo);

        book = Book.builder()
                .title("테스트 책")
                .index("목차")
                .description("설명")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .isbn("9781234567890")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(100)
                .build();
        ReflectionTestUtils.setField(book, "id", 1L);

    }

    @Test
    void 책생성시_메시지전달_성공() {

        when(categoryRepo.findCategoryNamesByBookId(1L)).thenReturn(List.of("Novel"));
        when(tagRepo.findTagNamesByBookId(1L)).thenReturn(List.of("Fantasy"));

        BookCreatedEvent event = new BookCreatedEvent(book);

        // when
        listener.handleBookCreated(event);

        // then
        var captor = ArgumentCaptor.forClass(BookInfoMqRequest.class);

        verify(publisher).sendBookTaskMessage(eq(EventType.INSERT), captor.capture());

        var msg = captor.getValue();

        assert msg.id() == (1L);
        assert msg.categories().contains("Novel");
        assert msg.tags().contains("Fantasy");
    }

    @Test
    void 책업데이트시_메시지전달_성공() {

        when(categoryRepo.findCategoryNamesByBookId(1L)).thenReturn(List.of("Sci-Fi"));
        when(tagRepo.findTagNamesByBookId(1L)).thenReturn(List.of("Space"));

        BookUpdatedEvent event = new BookUpdatedEvent(book);

        // when
        listener.handleBookUpdated(event);

        // then
        var captor = ArgumentCaptor.forClass(BookInfoMqRequest.class);

        verify(publisher).sendBookTaskMessage(eq(EventType.UPDATE), captor.capture());

        var msg = captor.getValue();

        assert msg.id() == 1L;
        assert msg.categories().contains("Sci-Fi");
        assert msg.tags().contains("Space");
    }

    @Test
    void 책삭제시_메시지전달_성공() {
        // given
        BookDeletedEvent event = new BookDeletedEvent(1L);

        // when
        listener.handleBookDeleted(event);

        // then
        var captor = ArgumentCaptor.forClass(BookDeleteMqRequest.class);

        verify(publisher).sendBookTaskMessage(eq(EventType.DELETE), captor.capture());

        var msg = captor.getValue();

        assert msg.id().equals(1L);
    }
}

