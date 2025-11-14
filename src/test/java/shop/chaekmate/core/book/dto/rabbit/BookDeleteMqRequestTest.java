package shop.chaekmate.core.book.dto.rabbit;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookDeleteMqRequestTest {

    @Test
    void of_메서드_테스트() {
        // given
        Long bookId = 10L;

        // when
        BookDeleteMqRequest dto = BookDeleteMqRequest.of(bookId);

        // then
        assertNotNull(dto);
        assertEquals("BOOK_DELETE", dto.dtoType());
        assertEquals(bookId, dto.id());
    }
}
