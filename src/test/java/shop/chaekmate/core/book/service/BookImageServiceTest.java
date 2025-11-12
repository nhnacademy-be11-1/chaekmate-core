package shop.chaekmate.core.book.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.core.book.dto.request.BookImageAddRequest;
import shop.chaekmate.core.book.dto.request.ThumbnailUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookImageResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.book.exception.BookImageNotFoundException;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.repository.BookImageQueryRepository;
import shop.chaekmate.core.book.repository.BookImageRepository;
import shop.chaekmate.core.book.repository.BookRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookImageServiceTest {

    @InjectMocks
    private BookImageService bookImageService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private BookImageQueryRepository bookImageQueryRepository;

    @Test
    void 이미지_추가_성공() {
        // given
        long bookId = 1L;
        long imageId = 100L;
        Book book = mock(Book.class);
        BookImageAddRequest request = new BookImageAddRequest("http://example.com/image.jpg");

        BookImage imageToSave = new BookImage(book, request.getImageUrl());
        ReflectionTestUtils.setField(imageToSave, "id", imageId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageRepository.save(any(BookImage.class))).thenReturn(imageToSave);
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(List.of(imageToSave));

        // when
        BookImageResponse response = bookImageService.addImage(bookId, request);

        // then
        assertAll(
                () -> assertThat(response.bookImageId()).isEqualTo(imageId),
                () -> assertThat(response.imageUrl()).isEqualTo(request.getImageUrl())
        );
        verify(bookRepository).findById(bookId);
        verify(bookImageRepository).save(any(BookImage.class));
        verify(bookImageQueryRepository).findAllByBookIdOrderByCreatedAtAsc(bookId);
    }

    @Test
    void 이미지_추가_실패_책을_찾을_수_없음() {
        // given
        long bookId = 1L;
        BookImageAddRequest request = new BookImageAddRequest("http://example.com/image.jpg");
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookImageService.addImage(bookId, request));
    }

    @Test
    void 썸네일_조회_성공() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        BookImage thumbnail = new BookImage(book, "http://example.com/thumb.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(List.of(thumbnail));

        // when
        BookImageResponse response = bookImageService.findThumbnail(bookId);

        // then
        assertAll(
                () -> assertThat(response.imageUrl()).isEqualTo(thumbnail.getImageUrl()),
                () -> assertThat(response.isThumbnail()).isTrue()
        );
    }

    @Test
    void 썸네일_조회_실패_이미지가_없음() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(Collections.emptyList());

        // when & then
        assertThrows(BookImageNotFoundException.class, () -> bookImageService.findThumbnail(bookId));
    }

    @Test
    void 상세_이미지_조회_성공() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        BookImage thumbnail = new BookImage(book, "thumb.jpg");
        BookImage detail1 = new BookImage(book, "detail1.jpg");
        BookImage detail2 = new BookImage(book, "detail2.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(List.of(thumbnail, detail1, detail2));

        // when
        List<BookImageResponse> responses = bookImageService.findDetailImages(bookId);

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> {
                    Assertions.assertNotNull(responses);
                    assertThat(responses.getFirst().imageUrl()).isEqualTo("detail1.jpg");
                },
                () -> {
                    Assertions.assertNotNull(responses);
                    assertThat(responses.get(1).imageUrl()).isEqualTo("detail2.jpg");
                }
        );
    }

    @Test
    void 상세_이미지_조회_성공_상세_이미지_없음() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        BookImage thumbnail = new BookImage(book, "thumb.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(List.of(thumbnail));

        // when
        List<BookImageResponse> responses = bookImageService.findDetailImages(bookId);

        // then
        assertThat(responses).isEmpty();
    }

    @Test
    void 썸네일_수정_성공() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        BookImage originalThumbnail = mock(BookImage.class);
        ThumbnailUpdateRequest request = new ThumbnailUpdateRequest("http://new.url/thumb.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId)).thenReturn(List.of(originalThumbnail));

        // when
        bookImageService.updateThumbnail(bookId, request);

        // then
        verify(originalThumbnail).updateUrl(request.getNewImageUrl());
    }

    @Test
    void 썸네일_수정_썸네일_없을때() {
        // given
        long bookId = 1L;
        Book book = mock(Book.class);
        ThumbnailUpdateRequest request = new ThumbnailUpdateRequest("http://new.url/thumb.jpg");

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(bookId))
                .thenReturn(Collections.emptyList());

        // when
        bookImageService.updateThumbnail(bookId, request);

        // then
        verify(bookImageRepository).save(any(BookImage.class)); // 저장이 호출되는지 확인
    }

    @Test
    void 이미지_삭제_성공() {
        // given
        long bookId = 1L;
        long imageId = 10L;
        Book book = mock(Book.class);
        BookImage imageToDelete = mock(BookImage.class);

        when(book.getId()).thenReturn(bookId);
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageRepository.findById(imageId)).thenReturn(Optional.of(imageToDelete));
        when(imageToDelete.getBook()).thenReturn(book);

        // when
        bookImageService.deleteImage(bookId, imageId);

        // then
        verify(bookImageRepository).delete(imageToDelete);
    }

    @Test
    void 이미지_삭제_실패_책을_찾을_수_없음() {
        // given
        long bookId = 1L;
        long imageId = 10L;
        when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookNotFoundException.class, () -> bookImageService.deleteImage(bookId, imageId));
        verify(bookImageRepository, never()).findById(anyLong());
        verify(bookImageRepository, never()).delete(any());
    }

    @Test
    void 이미지_삭제_실패_이미지를_찾을_수_없음() {
        // given
        long bookId = 1L;
        long imageId = 10L;
        Book book = mock(Book.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageRepository.findById(imageId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(BookImageNotFoundException.class, () -> bookImageService.deleteImage(bookId, imageId));
        verify(bookImageRepository, never()).delete(any());
    }

    @Test
    void 이미지_삭제_실패_이미지가_책에_속하지_않음() {
        // given
        long bookId = 1L;
        long anotherBookId = 2L;
        long imageId = 10L;
        Book book = mock(Book.class);
        Book anotherBook = mock(Book.class);
        BookImage image = mock(BookImage.class);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookImageRepository.findById(imageId)).thenReturn(Optional.of(image));
        when(image.getBook()).thenReturn(anotherBook);
        when(anotherBook.getId()).thenReturn(anotherBookId);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> bookImageService.deleteImage(bookId, imageId));
    }
}
