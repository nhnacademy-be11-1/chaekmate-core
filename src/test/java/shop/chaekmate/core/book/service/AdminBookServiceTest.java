package shop.chaekmate.core.book.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.dto.response.BookImageResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.exception.BookImageNotFoundException;
import shop.chaekmate.core.book.repository.AdminBookRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminBookServiceTest {

    @InjectMocks
    private AdminBookService adminBookService;

    @Mock
    private AdminBookRepositoryImpl adminBookRepository;

    @Mock
    private BookImageService bookImageService;

    @Test
    void 최신_도서_조회_성공() {
        // given
        int limit = 5;
        Book book1 = Book.builder().title("Book 1").author("Author 1").build();
        setPrivateField(book1, "id", 1L);
        setPrivateField(book1, "createdAt", LocalDateTime.now());

        Book book2 = Book.builder().title("Book 2").author("Author 2").build();
        setPrivateField(book2, "id", 2L);
        setPrivateField(book2, "createdAt", LocalDateTime.now().minusDays(1));

        List<Book> books = List.of(book1, book2);
        BookImageResponse bookImageResponse = BookImageResponse.builder()
                .imageUrl("imageUrl")
                .build();

        when(adminBookRepository.findRecentBooks(anyInt())).thenReturn(books);
        when(bookImageService.findThumbnail(1L)).thenReturn(bookImageResponse);
        doThrow(new BookImageNotFoundException()).when(bookImageService).findThumbnail(2L);


        // when
        List<AdminBookResponse> result = adminBookService.findRecentBooks(limit);

        // then
        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).title()).isEqualTo("Book 1"),
                () -> assertThat(result.get(1).title()).isEqualTo("Book 2"),
                () -> assertThat(result.get(0).imageUrl()).isEqualTo("imageUrl"),
                () -> assertThat(result.get(1).imageUrl()).isNull()
        );
    }

    private void setPrivateField(Object obj, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field;
            try {
                field = obj.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
                field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
            }
            field.setAccessible(true);
            field.set(obj, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
