package shop.chaekmate.core.book.repository;


import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, BookImageRepositoryImpl.class})
class BookImageRepositoryImplTest {

    private BookImageRepositoryImpl bookImageRepositoryImpl;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BookRepository bookRepository; // 실제 Book 엔티티 저장용

    @Autowired
    private BookImageRepository bookImageRepository;

    private Long savedBookId;

    @BeforeEach
    void setUp() {
        // 테스트용 Book과 Category 세팅
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

        // BookImage 매핑
        BookImage bc1 = new BookImage(book,"https://chaemkate.shop/images/test-image-url.jpg");
        bookImageRepository.save(bc1);

        BookImage bc2 = new BookImage(book,"https://chaemkate.shop/images/test-image-url2.jpg");
        bookImageRepository.save(bc2);

        // 리포지토리 초기화
        bookImageRepositoryImpl = new BookImageRepositoryImpl(queryFactory);
    }

    @Test
    void 책ID로_이미지_링크_전부_찾기_성공() {

        List<BookImage> bookImages = bookImageRepositoryImpl.findAllByBookIdOrderByCreatedAtAsc(savedBookId);

        List<String> imageNames = bookImages.stream().map(BookImage::getImageUrl).toList();

        assertThat(imageNames).containsExactlyInAnyOrder("https://chaemkate.shop/images/test-image-url.jpg", "https://chaemkate.shop/images/test-image-url2.jpg");
    }
}
