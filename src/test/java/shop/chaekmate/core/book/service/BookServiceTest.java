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
import org.springframework.test.util.ReflectionTestUtils;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.entity.*;
import shop.chaekmate.core.book.exception.*;
import shop.chaekmate.core.book.repository.*;
import shop.chaekmate.core.external.aladin.AladinBook;
import shop.chaekmate.core.external.aladin.AladinClient;
import shop.chaekmate.core.external.aladin.AladinSearchType;
import shop.chaekmate.core.external.aladin.dto.request.AladinBookRegisterRequest;
import shop.chaekmate.core.external.aladin.dto.response.AladinApiResponse;
import shop.chaekmate.core.external.aladin.dto.response.BookSearchResponse;
import shop.chaekmate.core.member.repository.AdminRepository;

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
import static org.mockito.Mockito.times;

/**
 * BookService 테스트 클래스
 *
 * @ExtendWith(MockitoExtension.class)
 * - JUnit 5에서 Mockito를 사용하기 위한 확장
 * - Mock 객체 자동 생성 및 주입 지원
 *
 * @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
 * - 테스트 메서드명의 언더스코어(_)를 공백으로 변환하여 표시
 * - 예: "도서_생성_성공" → "도서 생성 성공"
 */
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookServiceTest {

    /**
     * @InjectMocks
     * - 테스트 대상 클래스
     * - @Mock으로 선언된 의존성들이 자동으로 주입됨
     */
    @InjectMocks
    private BookService bookService;

    /**
     * @Mock
     * - 실제 객체 대신 가짜(Mock) 객체 생성
     * - 실제 DB나 외부 API 호출 없이 테스트 가능
     */
    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookImageRepository bookImageRepository;

    @Mock
    private AdminRepository adminRepository;

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

    // 테스트에서 공통으로 사용할 객체들
    private Book book;
    private BookImage bookImage;
    private Category category;
    private Tag tag;

    /**
     * @BeforeEach
     * - 각 테스트 메서드 실행 전에 실행됨
     * - 테스트에 필요한 공통 데이터 초기화
     */
    @BeforeEach
    void setUp() {
        // ReflectionTestUtils.setField()
        // - private 필드에 값을 직접 주입 (리플렉션 사용)
        // - @Value("${aladin.api.key}") 대신 테스트용 값 주입
        ReflectionTestUtils.setField(bookService, "aladinApiKey", "test-api-key");

        // 테스트용 Book 객체 생성 (Builder 패턴 사용)
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
        // Book의 id를 설정하기 위해 리플렉션 사용
        ReflectionTestUtils.setField(book, "id", 1L);

        // 테스트용 Category 객체 생성 (생성자 사용, parentCategory는 null)
        category = new Category(null, "소설");
        // Category의 id를 설정하기 위해 리플렉션 사용
        ReflectionTestUtils.setField(category, "id", 1L);

        // 테스트용 Tag 객체 생성 (생성자 사용)
        tag = new Tag("베스트셀러");
        // Tag의 id를 설정하기 위해 리플렉션 사용
        ReflectionTestUtils.setField(tag, "id", 1L);

        // 테스트용 BookImage 객체 생성 (생성자 사용)
        bookImage = new BookImage(book, "https://example.com/image.jpg");
    }

    // ========== createBook 테스트 ==========

    /**
     * 테스트: 도서 생성 성공 케이스
     *
     * 시나리오: 모든 필수 정보가 올바르게 입력되어 도서가 성공적으로 생성됨
     */
    @Test
    void 도서_생성_성공() {
        // given - 테스트 준비 단계
        // BookCreateRequest 생성 (DTO 필드 순서대로)
        BookCreateRequest request = new BookCreateRequest(
                "새 책",                                      // title
                "목차",                                       // index
                "설명",                                       // description
                "저자",                                       // author
                "출판사",                                     // publisher
                LocalDateTime.of(2024, 1, 1, 0, 0),         // publishedAt
                "9781234567890",                             // isbn
                10000,                                       // price
                9000,                                        // salesPrice
                "https://example.com/image.jpg",             // imageUrl
                true,                                        // isWrappable
                false,                                       // isSaleEnd
                100,                                         // stock
                List.of(1L),                                 // categoryIds
                List.of(1L)                                  // tagIds
        );

        // given().willReturn() - Mock 동작 정의
        // "bookRepository.save()가 호출되면 book 객체를 반환하라"
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // "categoryRepository.findAllById()가 호출되면 category 리스트를 반환하라"
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        // "tagRepository.findAllById()가 호출되면 tag 리스트를 반환하라"
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        // when - 실제 테스트 실행
        // 도서 생성 메서드 호출
        bookService.createBook(request);

        // then - 검증 단계
        // then().should() - 특정 메서드가 호출되었는지 검증

        // "bookRepository.save()가 정확히 1번 호출되었는가?"
        then(bookRepository).should(times(1)).save(any(Book.class));

        // "bookImageRepository.save()가 정확히 1번 호출되었는가?"
        then(bookImageRepository).should(times(1)).save(any(BookImage.class));

        // "bookCategoryRepository.save()가 정확히 1번 호출되었는가?"
        then(bookCategoryRepository).should(times(1)).save(any(BookCategory.class));

        // "bookTagRepository.save()가 정확히 1번 호출되었는가?"
        then(bookTagRepository).should(times(1)).save(any(BookTag.class));
    }

    /**
     * 테스트: 도서 생성 실패 - 카테고리 없음
     *
     * 시나리오: 요청한 카테고리 ID가 DB에 존재하지 않아 예외 발생
     */
    @Test
    void 도서_생성_실패_카테고리_없음() {
        // given
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
                List.of(1L, 2L),  // 카테고리 2개 요청
                List.of(1L)
        );

        // Book 저장은 성공
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // 하지만 카테고리는 1개만 반환 (2개 요청했는데 1개만 있음)
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        // when & then
        // assertThatThrownBy() - 예외가 발생하는지 검증
        assertThatThrownBy(() -> bookService.createBook(request))
                // 발생한 예외의 타입이 CategoryNotFoundException인가?
                .isInstanceOf(CategoryNotFoundException.class)
                // 예외 메시지가 정확한가?
                .hasMessage("일부 카테고리 ID를 찾을 수 없습니다.");
    }

    /**
     * 테스트: 도서 생성 실패 - 태그 없음
     *
     * 시나리오: 요청한 태그 ID가 DB에 존재하지 않아 예외 발생
     */
    @Test
    void 도서_생성_실패_태그_없음() {
        // given
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
                List.of(1L, 2L)  // 태그 2개 요청
        );

        // Book, Category는 성공
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        // 하지만 태그는 1개만 반환 (2개 요청했는데 1개만 있음)
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        // when & then
        // TagNotFoundException 예외가 발생해야 함
        assertThatThrownBy(() -> bookService.createBook(request))
                .isInstanceOf(TagNotFoundException.class)
                .hasMessage("일부 태그 ID를 찾을 수 없습니다.");
    }

    // ========== updateBook 테스트 ==========

    /**
     * 테스트: 도서 수정 성공
     *
     * 시나리오: 기존 도서 정보를 성공적으로 수정
     */
    @Test
    void 도서_수정_성공() {
        // given
        Long bookId = 1L;

        // 수정 요청 DTO (DTO 필드 순서대로)
        BookUpdateRequest request = new BookUpdateRequest(
                "수정된 책",
                "수정된 목차",
                "수정된 설명",
                "수정된 저자",
                "수정된 출판사",
                LocalDateTime.of(2024, 2, 1, 0, 0),
                "9781234567891",
                12000,
                10000,
                "https://example.com/new-image.jpg",
                false,
                true,
                50,
                List.of(1L),
                List.of(1L)
        );

        // Mock 동작 정의
        // "bookId로 책을 찾으면 book 객체를 반환하라"
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // "bookId로 이미지를 찾으면 bookImage 객체를 반환하라"
        given(bookImageRepository.findByBookId(bookId)).willReturn(Optional.of(bookImage));

        // "book의 카테고리를 조회하면 BookCategory 리스트를 반환하라"
        given(bookCategoryRepository.findByBook(book)).willReturn(List.of(new BookCategory(book, category)));

        // "book의 태그를 조회하면 BookTag 리스트를 반환하라"
        given(bookTagRepository.findByBook(book)).willReturn(List.of(new BookTag(book, tag)));

        // when
        bookService.updateBook(bookId, request);

        // then
        // findById()가 1번 호출되었는지 검증
        then(bookRepository).should(times(1)).findById(bookId);

        // findByBookId()가 1번 호출되었는지 검증
        then(bookImageRepository).should(times(1)).findByBookId(bookId);
    }

    /**
     * 테스트: 도서 수정 실패 - 책 없음
     *
     * 시나리오: 수정하려는 책이 DB에 존재하지 않음
     */
    @Test
    void 도서_수정_실패_책_없음() {
        // given
        Long bookId = 999L;  // 존재하지 않는 ID

        BookUpdateRequest request = new BookUpdateRequest(
                "수정된 책",
                null, null, null, null, null, null, null, null, null, null, null, null, null, null
        );

        // "bookId로 책을 찾으면 빈 Optional을 반환하라" (책이 없음)
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        // BookNotFoundException 예외가 발생해야 함
        assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book id 999 not found");
    }

    /**
     * 테스트: 도서 수정 실패 - 이미지 없음
     *
     * 시나리오: 책은 존재하지만 이미지가 없음
     */
    @Test
    void 도서_수정_실패_이미지_없음() {
        // given
        Long bookId = 1L;

        BookUpdateRequest request = new BookUpdateRequest(
                "수정된 책",
                null, null, null, null, null, null, null, null,
                "https://example.com/new-image.jpg",  // imageUrl만 수정
                null, null, null, null, null
        );

        // 책은 존재함
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // 하지만 이미지는 없음
        given(bookImageRepository.findByBookId(bookId)).willReturn(Optional.empty());

        // when & then
        // BookImageNotFoundException 예외가 발생해야 함
        assertThatThrownBy(() -> bookService.updateBook(bookId, request))
                .isInstanceOf(BookImageNotFoundException.class)
                .hasMessage("책 이미지를 찾을 수 없습니다.");
    }

    // ========== deleteBook 테스트 ==========

    /**
     * 테스트: 도서 삭제 성공
     *
     * 시나리오: 기존 도서를 성공적으로 삭제
     */
    @Test
    void 도서_삭제_성공() {
        // given
        Long bookId = 1L;

        // "bookId로 책을 찾으면 book 객체를 반환하라"
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));

        // "book을 삭제할 때 아무것도 하지 않는다" (정상 처리)
        // willDoNothing() - void 메서드의 정상 동작 정의
        willDoNothing().given(bookRepository).delete(book);

        // when
        bookService.deleteBook(bookId);

        // then
        // findById()가 1번 호출되었는지
        then(bookRepository).should(times(1)).findById(bookId);

        // delete()가 1번 호출되었는지
        then(bookRepository).should(times(1)).delete(book);
    }

    /**
     * 테스트: 도서 삭제 실패 - 책 없음
     *
     * 시나리오: 삭제하려는 책이 DB에 존재하지 않음
     */
    @Test
    void 도서_삭제_실패_책_없음() {
        // given
        Long bookId = 999L;  // 존재하지 않는 ID

        // 빈 Optional 반환 (책이 없음)
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.deleteBook(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("삭제할 책을 찾을 수 없습니다");
    }

    // ========== getBook 테스트 ==========

    /**
     * 테스트: 도서 조회 성공
     *
     * 시나리오: 책, 이미지, 카테고리, 태그 모두 정상 조회
     */
    @Test
    void 도서_조회_성공() {
        // given
        Long bookId = 1L;

        // Mock 동작 정의 (모든 데이터 정상 반환)
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookImageRepository.findByBookId(bookId)).willReturn(Optional.of(bookImage));
        given(bookCategoryRepository.findByBook(book)).willReturn(List.of(new BookCategory(book, category)));
        given(bookTagRepository.findByBook(book)).willReturn(List.of(new BookTag(book, tag)));

        // when
        BookResponse response = bookService.getBook(bookId);

        // then
        // assertThat() - AssertJ 라이브러리의 검증 메서드

        // response가 null이 아닌지
        assertThat(response).isNotNull();

        // title이 "테스트 책"인지
        assertThat(response.title()).isEqualTo("테스트 책");

        // imageUrl이 "https://example.com/image.jpg"인지
        assertThat(response.imageUrl()).isEqualTo("https://example.com/image.jpg");

        // categoryIds의 크기가 1인지
        assertThat(response.categoryIds()).hasSize(1);

        // tagIds의 크기가 1인지
        assertThat(response.tagIds()).hasSize(1);
    }

    /**
     * 테스트: 도서 조회 실패 - 책 없음
     */
    @Test
    void 도서_조회_실패_책_없음() {
        // given
        Long bookId = 999L;

        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.getBook(bookId))
                .isInstanceOf(BookNotFoundException.class)
                .hasMessageContaining("Book id 999 not found");
    }

    /**
     * 테스트: 도서 조회 실패 - 이미지 없음
     */
    @Test
    void 도서_조회_실패_이미지_없음() {
        // given
        Long bookId = 1L;

        // 책은 있지만 이미지는 없음
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(bookImageRepository.findByBookId(bookId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.getBook(bookId))
                .isInstanceOf(BookImageNotFoundException.class)
                .hasMessage("책 이미지를 찾을 수 없습니다.");
    }

    // ========== getBookList 테스트 ==========

    /**
     * 테스트: 도서 목록 조회 성공
     *
     * 시나리오: 페이징 처리된 도서 목록 정상 조회
     */
    @Test
    void 도서_목록_조회_성공() {
        // given
        // Pageable - 페이징 정보 (0페이지, 10개씩)
        Pageable pageable = PageRequest.of(0, 10);

        // Book 리스트 (테스트용으로 1개만)
        List<Book> books = List.of(book);

        // Page 객체 생성 (실제 서비스에서 반환하는 형태)
        Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

        // Mock 동작 정의
        given(bookRepository.findAll(pageable)).willReturn(bookPage);
        given(bookImageRepository.findByBookId(book.getId())).willReturn(Optional.of(bookImage));
        given(bookCategoryRepository.findByBook(book)).willReturn(List.of(new BookCategory(book, category)));
        given(bookTagRepository.findByBook(book)).willReturn(List.of(new BookTag(book, tag)));

        // when
        Page<BookResponse> result = bookService.getBookList(pageable);

        // then
        // result가 null이 아닌지
        assertThat(result).isNotNull();

        // content의 크기가 1인지 (1개의 책)
        assertThat(result.getContent()).hasSize(1);

        // 전체 개수가 1인지
        assertThat(result.getTotalElements()).isEqualTo(1);

        // 첫 번째 책의 제목이 "테스트 책"인지
        assertThat(result.getContent().get(0).title()).isEqualTo("테스트 책");
    }

    /**
     * 테스트: 도서 목록 조회 실패 - 이미지 없음
     */
    @Test
    void 도서_목록_조회_실패_이미지_없음() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

        given(bookRepository.findAll(pageable)).willReturn(bookPage);

        // 이미지가 없음
        given(bookImageRepository.findByBookId(book.getId())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.getBookList(pageable))
                .isInstanceOf(BookImageNotFoundException.class)
                .hasMessage("책 이미지를 찾을 수 없습니다.");
    }

    // ========== searchFromAladin 테스트 ==========

    /**
     * 테스트: 알라딘 검색 성공
     *
     * 시나리오: 알라딘 API 호출 성공 및 결과 반환
     */
    @Test
    void 알라딘_검색_성공() {
        // given
        String query = "어린왕자";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        // 알라딘 API 응답 데이터 생성 (가짜)
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
                50,   // totalResults - 전체 50개
                1,    // startIndex - 1페이지
                10,   // itemsPerPage - 10개씩
                List.of(aladinBook)  // items - 책 1권
        );

        // "aladinClient.searchBooks()가 호출되면 apiResponse를 반환하라"
        // anyString(), anyInt() - 어떤 값이든 상관없이 매칭
        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(apiResponse);

        // when
        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        // then
        assertThat(result).isNotNull();

        // 검색 결과가 1개인지
        assertThat(result.getContent()).hasSize(1);

        // 전체 개수가 50개인지
        assertThat(result.getTotalElements()).isEqualTo(50);

        // 첫 번째 책의 제목이 "어린 왕자"인지
        assertThat(result.getContent().get(0).title()).isEqualTo("어린 왕자");
    }

    /**
     * 테스트: 알라딘 검색 - 결과 없음
     *
     * 시나리오: API 응답이 null인 경우
     */
    @Test
    void 알라딘_검색_결과_없음() {
        // given
        String query = "존재하지않는책";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        // API 응답이 null (검색 결과 없음)
        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(null);

        // when
        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        // then
        assertThat(result).isNotNull();

        // 빈 리스트인지
        assertThat(result.getContent()).isEmpty();

        // 전체 개수가 0인지
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    /**
     * 테스트: 알라딘 검색 - items가 null
     *
     * 시나리오: API 응답은 있지만 items가 null인 경우
     */
    @Test
    void 알라딘_검색_items가_null() {
        // given
        String query = "어린왕자";
        AladinSearchType searchType = AladinSearchType.TITLE;
        Pageable pageable = PageRequest.of(0, 10);

        // items가 null인 응답
        AladinApiResponse apiResponse = new AladinApiResponse(0, 0, 0, null);

        given(aladinClient.searchBooks(
                anyString(), anyString(), anyString(),
                anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(apiResponse);

        // when
        Page<BookSearchResponse> result = bookService.searchFromAladin(query, searchType, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    // ========== registerFromAladin 테스트 ==========

    /**
     * 테스트: 알라딘 도서 등록 성공
     *
     * 시나리오: 알라딘에서 검색한 책을 우리 DB에 등록 성공
     */
    @Test
    void 알라딘_도서_등록_성공() {
        // given
        Long adminId = 1L;

        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",                    // title
                "목차",                         // index
                "설명",                         // description
                "생텍쥐페리",                   // author
                "비룡소",                       // publisher
                "2000-05-23",                   // publishedAt
                "9788949190136",                // isbn
                14000,                          // price
                12600,                          // salesPrice
                true,                           // isWrappable
                false,                          // isSaleEnd
                "https://example.com/image.jpg", // imageUrl
                100,                            // stock
                List.of(1L),                    // categoryIds
                List.of(1L)                     // tagIds
        );

        // Mock 동작 정의
        given(adminRepository.existsById(adminId)).willReturn(true);
        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));
        given(tagRepository.findAllById(anyList())).willReturn(List.of(tag));

        // when
        bookService.registerFromAladin(adminId, request);

        // then
        // 각 저장 메서드가 1번씩 호출되었는지 검증
        then(bookRepository).should(times(1)).save(any(Book.class));
        then(bookImageRepository).should(times(1)).save(any(BookImage.class));
        then(bookCategoryRepository).should(times(1)).save(any(BookCategory.class));
        then(bookTagRepository).should(times(1)).save(any(BookTag.class));
    }

    /**
     * 테스트: 알라딘 도서 등록 실패 - 관리자 없음
     */
    @Test
    void 알라딘_도서_등록_실패_관리자_없음() {
        // given
        Long adminId = 999L;  // 존재하지 않는 관리자

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

        // 관리자가 존재하지 않음
        given(adminRepository.existsById(adminId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> bookService.registerFromAladin(adminId, request))
                .isInstanceOf(AdminNotFoundException.class)
                .hasMessageContaining("관리자를 찾을 수 없습니다");
    }

    /**
     * 테스트: 알라딘 도서 등록 실패 - ISBN 중복
     */
    @Test
    void 알라딘_도서_등록_실패_ISBN_중복() {
        // given
        Long adminId = 1L;

        AladinBookRegisterRequest request = new AladinBookRegisterRequest(
                "어린 왕자",
                null, null,
                "생텍쥐페리",
                null,
                "2000-05-23",
                "9788949190136",  // 이미 존재하는 ISBN
                14000, 12600,
                true, false,
                "https://example.com/image.jpg",
                100,
                List.of(1L),
                null
        );

        // 관리자는 존재함
        given(adminRepository.existsById(adminId)).willReturn(true);

        // 하지만 ISBN이 이미 존재함
        given(bookRepository.existsByIsbn(request.isbn())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> bookService.registerFromAladin(adminId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 등록된 ISBN입니다");
    }

    /**
     * 테스트: 알라딘 도서 등록 - 태그가 null
     *
     * 시나리오: 태그가 null일 때 태그 저장을 건너뛰는지 확인
     */
    @Test
    void 알라딘_도서_등록_태그가_null() {
        // given
        Long adminId = 1L;

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
                null  // tagIds가 null
        );

        given(adminRepository.existsById(adminId)).willReturn(true);
        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        // when
        bookService.registerFromAladin(adminId, request);

        // then
        // never() - 한 번도 호출되지 않았는지 검증
        // 태그가 null이므로 bookTagRepository.save()가 호출되지 않아야 함
        then(bookTagRepository).should(never()).save(any(BookTag.class));
    }

    /**
     * 테스트: 알라딘 도서 등록 - 태그가 빈 리스트
     *
     * 시나리오: 태그가 빈 리스트일 때 태그 저장을 건너뛰는지 확인
     */
    @Test
    void 알라딘_도서_등록_태그가_빈_리스트() {
        // given
        Long adminId = 1L;

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
                new ArrayList<>()  // 빈 리스트
        );

        given(adminRepository.existsById(adminId)).willReturn(true);
        given(bookRepository.existsByIsbn(request.isbn())).willReturn(false);
        given(bookRepository.save(any(Book.class))).willReturn(book);
        given(categoryRepository.findAllById(anyList())).willReturn(List.of(category));

        // when
        bookService.registerFromAladin(adminId, request);

        // then
        // 빈 리스트이므로 bookTagRepository.save()가 호출되지 않아야 함
        then(bookTagRepository).should(never()).save(any(BookTag.class));
    }
}