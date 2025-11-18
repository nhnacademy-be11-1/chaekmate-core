package shop.chaekmate.core.book.repository;


import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.AdminBookPagedRequest;
import shop.chaekmate.core.book.dto.request.BookSortType;
import shop.chaekmate.core.book.dto.response.AdminBookResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, AdminBookRepositoryImpl.class})
class AdminBookRepositoryImplTest {

    @Autowired
    private AdminBookRepositoryImpl adminBookRepositoryImpl;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookImageRepository bookImageRepository;

    private Long savedBookId;

    @BeforeEach
    void setUp() {

        Book book = Book.builder()
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

        book = bookRepository.save(book);
        savedBookId = book.getId();

        // 이미지 2개 저장
        BookImage img1 = new BookImage(book, "https://test.com/1.jpg");
        bookImageRepository.save(img1);

        BookImage img2 = new BookImage(book, "https://test.com/2.jpg");
        bookImageRepository.save(img2);
    }

    @Test
    void 최근_도서_조회_성공() {
        List<Book> recentBooks = adminBookRepositoryImpl.findRecentBooks(5);

        assertAll(
                () -> AssertionsForInterfaceTypes.assertThat(recentBooks).hasSize(1),
                () -> AssertionsForClassTypes.assertThat(recentBooks.getFirst().getId()).isEqualTo(savedBookId)
        );
    }

    @Test
    void 페이징_도서_조회_성공() {
        // given
        AdminBookPagedRequest req = new AdminBookPagedRequest();
        req.setPage(0);
        req.setSize(10);
        req.setKeyword("테스트");
        req.setSortType(BookSortType.RECENT);

        // when
        Page<AdminBookResponse> result = adminBookRepositoryImpl.findBooks(req);

        // then
        AdminBookResponse book = result.getContent().getFirst();

        assertAll(
                () -> AssertionsForInterfaceTypes.assertThat(result.getContent()).hasSize(1),
                () -> AssertionsForClassTypes.assertThat(book.id()).isEqualTo(savedBookId),
                () -> AssertionsForClassTypes.assertThat(book.title()).isEqualTo("테스트 책"),
                () -> AssertionsForClassTypes.assertThat(book.author()).isEqualTo("테스트 저자"),
                () -> AssertionsForClassTypes.assertThat(book.imageUrl()).isEqualTo("https://test.com/1.jpg"),
                () -> AssertionsForClassTypes.assertThat(book.reviewCount()).isZero()
        );
    }
}
