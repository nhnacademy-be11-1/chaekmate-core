package shop.chaekmate.core.book.repository;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
/*

@DataJpaTest
@Import({QueryDslConfig.class, JpaAuditingConfig.class, BookImageQueryRepository.class})
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookImageQueryRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookImageRepository bookImageRepository;

    @Autowired
    private BookImageQueryRepository bookImageQueryRepository;

    @Test
    void 책_ID로_이미지_조회_시_생성_시간_오름차순_정렬_성공() {
        // given
        Book book = bookRepository.save(Book.builder()
                .isbn("12345")
                .title("Test Book")
                .author("Author")
                .publisher("Publisher")
                .index("Test Index")
                .description("Test Description")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0L)
                .isSaleEnd(false)
                .stock(10)
                .build());

        bookImageRepository.saveAndFlush(new BookImage(book, "url1"));

        // when
        List<BookImage> result = bookImageQueryRepository.findAllByBookIdOrderByCreatedAtAsc(book.getId());

        // then
        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result).extracting(BookImage::getImageUrl)
                        .containsExactly("url1")
        );
    }
}
*/
