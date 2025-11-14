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
import shop.chaekmate.core.book.entity.BookTag;
import shop.chaekmate.core.book.entity.Tag;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;


@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, BookTagRepositoryImpl.class})
class BookTagRepositoryImplTest {

    private BookTagRepositoryImpl bookTagRepositoryImpl;

    @Autowired
    private JPAQueryFactory queryFactory;

    @Autowired
    private BookRepository bookRepository; // 실제 Book 엔티티 저장용
    @Autowired
    private TagRepository tagRepository; // 실제 Tag 저장용
    @Autowired
    private BookTagRepository bookTagRepository; // 필요 시

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

        Tag tag1 = new Tag("Fiction");
        tag1 = tagRepository.save(tag1);

        Tag tag2 = new Tag("Adventure");
        tag2 = tagRepository.save(tag2);

        // BookTag 매핑
        BookTag bc1 = new BookTag(book,tag1);
        bookTagRepository.save(bc1);

        BookTag bc2 = new BookTag(book,tag2);

        bookTagRepository.save(bc2);

        // 리포지토리 초기화
        bookTagRepositoryImpl = new BookTagRepositoryImpl(queryFactory);
    }

    @Test
    void 책ID로_태그이름_찾기_성공() {

        List<String> tagNames = bookTagRepositoryImpl.findTagNamesByBookId(savedBookId);

        assertThat(tagNames).containsExactlyInAnyOrder("Fiction", "Adventure");
    }
}
