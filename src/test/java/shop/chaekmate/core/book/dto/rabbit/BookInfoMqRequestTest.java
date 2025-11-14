package shop.chaekmate.core.book.dto.rabbit;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.core.book.entity.Book;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookInfoMqRequestTest {

    private Book book;

    @BeforeEach
    void setUp() {
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
        // id는 reflection으로 세팅
        ReflectionTestUtils.setField(book, "id", 1L);
    }

    @Test
    void of_메서드_커버리지_테스트() {
        // given
        List<String> categories = List.of("Novel");
        List<String> tags = List.of("Fantasy");
        String bookImages = "https://example.com/image.jpg";

        // when
        BookInfoMqRequest dto = BookInfoMqRequest.of(book, bookImages, categories, tags);

        // then
        assertNotNull(dto);
        assertEquals("BOOK_INFO", dto.dtoType());
        assertEquals(1L, dto.id());
        assertEquals("테스트 책", dto.title());
        assertEquals("테스트 저자", dto.author());
        assertEquals(10000, dto.price());
        assertEquals("설명", dto.description());
        assertEquals(bookImages, dto.bookImages());
        assertEquals(categories, dto.categories());
        assertEquals(LocalDate.of(2024, 1, 1), dto.publicationDatetime());
        assertEquals("9781234567890", dto.isbn());
        assertEquals("테스트 출판사", dto.publisher());
        assertEquals(tags, dto.tags());
        assertNull(dto.reviewSummary());
        assertNull(dto.rating());
        assertNull(dto.reviewCnt());
    }
}
