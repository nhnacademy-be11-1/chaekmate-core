package shop.chaekmate.core.book.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.response.BookQueryResponse;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.review.repository.ReviewRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, BookRepositoryImpl.class})
class BookQueryRepositoryTest {

    @Autowired
    private BookRepositoryImpl bookRepositoryImpl;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderedBookRepository orderedBookRepository;
    @Autowired
    private ReviewRepository reviewRepository;

    private Member member1, member2;
    private Book book1, book2, book3, book4;

    private Book createBook(String title, LocalDateTime publishedAt, String isbn, long views, int stock) {
        return Book.builder()
                .title(title)
                .publishedAt(publishedAt)
                .isbn(isbn)
                .views(views)
                .stock(stock)
                .index("index")
                .description("description")
                .author("author")
                .publisher("publisher")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .isSaleEnd(false)
                .build();
    }

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(new Member("id1", "pwd", "name1", "0101", "e1@mail.com", LocalDate.now(), PlatformType.LOCAL));
        member2 = memberRepository.save(new Member("id2", "pwd", "name2", "0102", "e2@mail.com", LocalDate.now(), PlatformType.LOCAL));

        book1 = bookRepository.save(createBook("Bestseller & High-Review Book", LocalDateTime.now().minusDays(20), "1", 100, 10));
        book2 = bookRepository.save(createBook("Old, No-Stock, Low-View Book", LocalDateTime.now().minusDays(40), "2", 10, 0));
        book3 = bookRepository.save(createBook("Newest, High-View Book", LocalDateTime.now(), "3", 500, 5));
        book4 = bookRepository.save(createBook("Recent, In-Stock Book", LocalDateTime.now().minusDays(5), "4", 200, 20));

        Order order1 = orderRepository.save(Order.createOrderReady(member1, "order1", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook ob1 = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order1, book1, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
        reviewRepository.save(Review.createReview(member1, ob1, "good book", 5));

        Order order2 = orderRepository.save(Order.createOrderReady(member2, "order2", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        orderedBookRepository.save(OrderedBook.createOrderDetailReady(order2, book1, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));

        Order order3 = orderRepository.save(Order.createOrderReady(member1, "order3", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook ob3 = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order3, book3, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
        reviewRepository.save(Review.createReview(member1, ob3, "fun", 3));
    }

    @Test
    void 최근_추가된_도서_조회() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findRecentlyAddedBooks(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book4.getId(), book3.getId());
    }

    @Test
    void 신간_도서_조회() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findNewBooks(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book3.getId(), book4.getId());
    }

    @Test
    void 베스트셀러_조회() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findBestsellers(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book1.getId(), book3.getId());
    }
    
    @Test
    void 전체도서_조회_검색조건_포함() {
        Pageable pageable = PageRequest.of(0, 10);
        BookSearchCondition condition = new BookSearchCondition(null, null, "Bestseller");
        Slice<BookQueryResponse> result = bookRepositoryImpl.findAllBooks(condition, pageable);
        assertAll(
                () -> assertThat(result.getContent()).hasSize(1),
                () -> assertThat(result.getContent().getFirst().id()).isEqualTo(book1.getId())
        );
    }

    @Test
    void 삼십일간_리뷰_많은_책_조회() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findTopReviewedBooksForLast30Days(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book1.getId(), book3.getId());
    }

    @Test
    void 조회수_기준_랭킹_조회() {
        Pageable pageable = PageRequest.of(1, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findBooksByViews(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book1.getId(), book2.getId()); // 조회수 3 ,4 등
    }

    @Test
    void 얼리어답터의_픽_조회() {
        Pageable pageable = PageRequest.of(0, 2);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findEarlyAdopterPicks(pageable);
        List<Long> ids = result.getContent().stream().map(BookQueryResponse::id).toList();
        assertThat(ids).containsExactly(book1.getId(), book3.getId());
    }

    @Test
    void 재고있는_책_랜덤_조회() {
        Pageable pageable = PageRequest.of(0, 3);
        Slice<BookQueryResponse> result = bookRepositoryImpl.findRandomInStockBooks(pageable);
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().stream().map(BookQueryResponse::id))
                .containsExactlyInAnyOrder(book1.getId(), book3.getId(), book4.getId());
    }
}
