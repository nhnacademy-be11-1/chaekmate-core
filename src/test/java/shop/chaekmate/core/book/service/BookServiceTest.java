package shop.chaekmate.core.book.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.entity.*;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.exception.CategoryNotFoundException;
import shop.chaekmate.core.book.exception.TagNotFoundException;
import shop.chaekmate.core.book.repository.*;
import shop.chaekmate.core.external.aladin.AladinBook;
import shop.chaekmate.core.external.aladin.AladinClient;
import shop.chaekmate.core.external.aladin.AladinSearchType;
import shop.chaekmate.core.external.aladin.dto.request.AladinBookRegisterRequest;
import shop.chaekmate.core.external.aladin.dto.response.AladinApiResponse;
import shop.chaekmate.core.external.aladin.dto.response.BookSearchResponse;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private BookCategoryRepository bookCategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private BookTagRepository bookTagRepository;

    @Mock
    private AladinClient aladinClient;

    private Book book;
    private BookImage bookImage;
    private Category category;
    private Tag tag;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(bookService, "aladinApiKey", "test-api-key");

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

        category = new Category(null, "소설");
        ReflectionTestUtils.setField(category, "id", 1L);

        tag = new Tag("베스트셀러");
        ReflectionTestUtils.setField(tag, "id", 1L);

        bookImage = new BookImage(book, "https://example.com/image.jpg");
    }

    @Test
    void 도서_생성_성공() {
        BookCreateRequest request = new BookCreateRequest(
                "새 책",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                "9781234567890",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(1L),
                List.of(1L)
        );

        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        bookService.createBook(request);

        then(bookRepository).should().save(any(Book.class));
        then(bookImageRepository).should().save(any(BookImage.class));
        then(bookCategoryRepository).should().save(any(BookCategory.class));
        then(bookTagRepository).should().save(any(BookTag.class));
    }

    @Test
    void 도서_생성_실패_카테고리_없음() {
        BookCreateRequest request = new BookCreateRequest(
                "새 책",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                "9781234567890",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(1L, 2L),
                List.of(1L)
        );

        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(CategoryNotFoundException.class)
                .hasMessage("일부 카테고리 ID를 찾을 수 없습니다.");
    }

    @Test
    void 도서_생성_실패_태그_없음() {
        BookCreateRequest request = new BookCreateRequest(
                "새 책",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                "9781234567890",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(1L),
                List.of(1L, 2L)
        );

        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessage("일부 태그 ID를 찾을 수 없습니다.");
    }

    @Test
    void 도서_수정_실패_책_없음() {
        Long bookId = 10000000000L;

        BookUpdateRequest request = new BookUpdateRequest(
                "수정된 책",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0),
                "9781234567890",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(1L),
                List.of(1L)
        );

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book id 10000000000 not found");
    }


    @Test
    void 도서_삭제_성공() {
        Long bookId = 1L;

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        willDoNothing().given(bookRepository).delete(book);

        bookService.deleteBook(bookId);

        then(bookRepository).should().findById(bookId);
        then(bookRepository).should().delete(book);
    }

    @Test
    void 도서_삭제_실패_책_없음() {
        Long bookId = 10000000000L;

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.deleteBook(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("삭제할 책을 찾을 수 없습니다");
    }

    @Test
    void 도서_조회_성공() {
        Long bookId = 1L;

        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookImageRepository.findByBookId(bookId)).willReturn(Optional.of(bookImage));
        given(bookCategoryRepository.findByBook(book)).willReturn(List.of(new BookCategory(book, category)));
        given(bookTagRepository.findByBook(book)).willReturn(List.of(new BookTag(book, tag)));

        BookResponse response = bookService.getBook(bookId);

        assertThat(response).isNotNull();
        assertThat(response.title()).isEqualTo("테스트 책");
        assertThat(response.imageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(response.categoryIds()).hasSize(1);
        assertThat(response.tagIds()).hasSize(1);
    }

    @Test
    void 도서_조회_실패_책_없음() {
        Long bookId = 10000000000L;

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> bookService.getBook(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book id 10000000000 not found");
    }

    @Test
    void 도서_목록_조회_성공() {
        Pageable pageable = PageRequest.of(0, 10);

        BookSearchCondition condition = new BookSearchCondition(
                null,
                null,
                null
        );

        BookListResponse bookListResponse = new BookListResponse(
                1L,
                "테스트 책",
                "테스트 저자",
                "테스트 출판사",
                10000,
                9000,
                "https://example.com/test-book.jpg"
        );

        Page<BookListResponse> expectedPage = new PageImpl<>(
                List.of(bookListResponse),
                pageable,
                1
        );

        given(bookRepository.searchBooks(any(BookSearchCondition.class), any(Pageable.class)))
                .willReturn(expectedPage);

        Page<BookListResponse> result = bookService.getBookList(condition, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().title()).isEqualTo("테스트 책");
        assertThat(result.getContent().getFirst().author()).isEqualTo("테스트 저자");
        assertThat(result.getContent().getFirst().publisher()).isEqualTo("테스트 출판사");
        assertThat(result.getContent().getFirst().salesPrice()).isEqualTo(9000);
        assertThat(result.getContent().getFirst().imageUrl()).isEqualTo("https://example.com/test-book.jpg");

        then(bookRepository).should().searchBooks(any(BookSearchCondition.class), any(Pageable.class));
    }

    @Test
    void 도서_목록_조회_카테고리_필터링() {
        Pageable pageable = PageRequest.of(0, 10);

        BookSearchCondition condition = new BookSearchCondition(
                1L,
                null,
                null
        );

        BookListResponse bookListResponse = new BookListResponse(
                1L,
                "테스트 책",
                "테스트 저자",
                "테스트 출판사",
                10000,
                9000,
                "https://example.com/test-book.jpg"
        );

        Page<BookListResponse> expectedPage = new PageImpl<>(
                List.of(bookListResponse),
                pageable,
                1
        );

        given(bookRepository.searchBooks(any(BookSearchCondition.class), any(Pageable.class)))
                .willReturn(expectedPage);

        Page<BookListResponse> result = bookService.getBookList(condition, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        then(bookRepository).should().searchBooks(any(BookSearchCondition.class), any(Pageable.class));
    }

    @Test
    void 도서_목록_조회_키워드_검색() {
        Pageable pageable = PageRequest.of(0, 10);

        BookSearchCondition condition = new BookSearchCondition(
                null,
                null,
                "자바"
        );

        BookListResponse bookListResponse = new BookListResponse(
                1L,
                "이펙티브 자바",
                "조슈아 블로크",
                "인사이트",
                10000,
                32400,
                "https://example.com/test-book.jpg"
        );

        Page<BookListResponse> expectedPage = new PageImpl<>(
                List.of(bookListResponse),
                pageable,
                1
        );

        given(bookRepository.searchBooks(any(BookSearchCondition.class), any(Pageable.class)))
                .willReturn(expectedPage);

        Page<BookListResponse> result = bookService.getBookList(condition, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().title()).contains("자바");
        then(bookRepository).should().searchBooks(any(BookSearchCondition.class), any(Pageable.class));
    }

    @Test
    void 도서_목록_조회_빈_결과() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        BookSearchCondition condition = new BookSearchCondition(
                10000000000L,
                null,
                null
        );

        Page<BookListResponse> emptyPage = new PageImpl<>(
                new ArrayList<>(),
                pageable,
                0
        );

        given(bookRepository.searchBooks(any(BookSearchCondition.class), any(Pageable.class)))
                .willReturn(emptyPage);

        Page<BookListResponse> result = bookService.getBookList(condition, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        then(bookRepository).should().searchBooks(any(BookSearchCondition.class), any(Pageable.class));
    }

    @Test
    void 알라딘_검색_성공() {
        String query = "어린왕자";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        AladinBook aladinBook = new AladinBook(
                "어린 왕자",
                "생텍쥐페리",
                "비룡소",
                "2000-05-23",
                "9788949190136",
                14000,
                12600,
                "https://example.com/cover.jpg",
                "프랑스 소설가...",
                "국내도서>소설"
        );

        AladinApiResponse apiResponse = new AladinApiResponse(
                50,
                1,
                10,
                List.of(aladinBook)
        );

        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(apiResponse);

        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(50);
        assertThat(result.getContent().getFirst().title()).isEqualTo("어린 왕자");
    }

    @Test
    void 알라딘_검색_결과_없음() {
        String query = "존재하지않는책";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(null);

        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void 알라딘_검색_items가_null() {
        String query = "어린왕자";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        AladinApiResponse apiResponse = new AladinApiResponse(0, 0, 0, null);

        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(apiResponse);

        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void 알라딘_도서_등록_성공() {
        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",
                "목차",
                "설명",
                "생텍쥐페리",
                "비룡소",
                "2000-05-23",
                "9788949190136",
                14000,
                12600,
                true,
                false,
                "https://example.com/image.jpg",
                100,
                List.of(1L),
                List.of(1L)
        );

        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        bookService.registerFromAladin(request);

        then(bookRepository).should().save(any(Book.class));
        then(bookImageRepository).should().save(any(BookImage.class));
        then(bookCategoryRepository).should().save(any(BookCategory.class));
        then(bookTagRepository).should().save(any(BookTag.class));
    }

    @Test
    void 알라딘_도서_등록_실패_ISBN_중복() {
        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",
                null, null,
                "생텍쥐페리",
                null,
                "2000-05-23",
                "9788949190136",
                14000, 12600,
                true, false,
                "https://example.com/image.jpg",
                100,
                List.of(1L),
                null
        );

        given(bookRepository.existsByIsbn(request.isbn())).willReturn(true);

        assertThatThrownBy(() -> bookService.registerFromAladin(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 ISBN입니다");
    }

    @Test
    void 알라딘_도서_등록_태그가_null() {
        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",
                null, null,
                "생텍쥐페리",
                null,
                "2000-05-23",
                "9788949190136",
                14000, 12600,
                true, false,
                "https://example.com/image.jpg",
                100,
                List.of(1L),
                null
        );

        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        bookService.registerFromAladin(request);

        then(bookTagRepository).should(never()).save(any(BookTag.class));
    }

    @Test
    void 알라딘_도서_등록_태그가_빈_리스트() {
        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",
                null, null,
                "생텍쥐페리",
                null,
                "2000-05-23",
                "9788949190136",
                14000, 12600,
                true, false,
                "https://example.com/image.jpg",
                100,
                List.of(1L),
                new ArrayList<>()
        );

        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        bookService.registerFromAladin(request);

        then(bookTagRepository).should(never()).save(any(BookTag.class));
    }
}
