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
import shop.chaekmate.core.book.entity.BookCategory;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, BookCategoryRepositoryImpl.class})
class BookCategoryRepositoryImplTest {

    private BookCategoryRepositoryImpl bookCategoryRepositoryImpl;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BookRepository bookRepository; // 실제 Book 엔티티 저장용
    @Autowired
    private CategoryRepository categoryRepository; // 실제 Category 저장용
    @Autowired
    private BookCategoryRepository bookCategoryRepository; // JPARepository

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

        Category category1 = new Category(null,"Fiction");
        category1 = categoryRepository.save(category1);

        Category category2 = new Category(null,"Adventure");
        category2 = categoryRepository.save(category2);

        // BookCategory 매핑
        BookCategory bc1 = new BookCategory(book,category1);
        bookCategoryRepository.save(bc1);

        BookCategory bc2 = new BookCategory(book,category2);

        bookCategoryRepository.save(bc2);

        // 리포지토리 초기화
        bookCategoryRepositoryImpl = new BookCategoryRepositoryImpl(queryFactory);
    }

    @Test
    void 책ID로_카테고리이름_찾기_성공() {
        List<String> categoryNames = bookCategoryRepositoryImpl.findCategoryNamesByBookId(savedBookId);

        assertThat(categoryNames).containsExactlyInAnyOrder("Fiction", "Adventure");
    }
}
