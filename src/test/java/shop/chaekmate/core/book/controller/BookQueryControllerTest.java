package shop.chaekmate.core.book.controller;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.entity.Review;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.order.repository.ReviewRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;
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
    @Autowired
    CacheManager cacheManager;

    @AfterEach
    void clearCache() {
        cacheManager.getCacheNames().forEach(name ->
                Objects.requireNonNull(cacheManager.getCache(name)).clear()
        );
    }

    private Member member1, member2;
    private Book book1, book2, book3, book4;

    @BeforeEach
    void setUp() {
        // 기본적으론 id가 높은(최근것) 이 앞선다

        // Members
        member1 = memberRepository.save(new Member("id1", "pwd", "name1", "0101", "e1@mail.com", LocalDate.now(), PlatformType.LOCAL));
        member2 = memberRepository.save(new Member("id2", "pwd", "name2", "0102", "e2@mail.com", LocalDate.now(), PlatformType.LOCAL));

        // Books
        book1 = bookRepository.save(new Book("Bestseller & High-Review Book", "i", "d", "a", "p", LocalDateTime.now().minusDays(20), "1", 10000, 9000, true, 100, false, 10));
        book2 = bookRepository.save(new Book("Old, No-Stock, Low-View Book", "i", "d", "a", "p", LocalDateTime.now().minusDays(40), "2", 10000, 9000, true, 10, false, 0));
        book3 = bookRepository.save(new Book("Newest, High-View Book", "i", "d", "a", "p", LocalDateTime.now(), "3", 10000, 9000, true, 500, false, 5));
        book4 = bookRepository.save(new Book("Recent, In-Stock Book", "i", "d", "a", "p", LocalDateTime.now().minusDays(5), "4", 10000, 9000, true, 200, false, 20));

        // --- Test Data Setup ---
        // Order & Review data is created within the last 30 days.

        // book1: 2 orders by 2 members, 2 reviews
        Order order1 = orderRepository.save(Order.createOrderReady(member1, "order1", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook ob1 = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order1, book1, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
        reviewRepository.save(new Review(member1, ob1, "good book", 5));

        Order order2 = orderRepository.save(Order.createOrderReady(member2, "order2", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook ob2 = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order2, book1, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
        reviewRepository.save(new Review(member2, ob2, "nice book", 4));

        // book3: 1 order by 1 member, 1 review
        Order order3 = orderRepository.save(Order.createOrderReady(member1, "order3", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook ob3 = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order3, book3, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
        reviewRepository.save(new Review(member1, ob3, "fun", 3));

        // book4: 1 order by 1 member, no reviews
        Order order4 = orderRepository.save(Order.createOrderReady(member2, "order4", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        orderedBookRepository.save(OrderedBook.createOrderDetailReady(order4, book4, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000));
    }

    @Test
    void 최근_추가된_도서_조회() throws Exception {
        mockMvc.perform(get("/books/recent").param("size", "2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book4.getId())) // Newest
                .andExpect(jsonPath("$.data.content[1].id").value(book3.getId()));
    }

    @Test
    void 신간_도서_조회() throws Exception {
        mockMvc.perform(get("/books/new-releases").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book3.getId()))
                .andExpect(jsonPath("$.data.content[1].id").value(book4.getId()));
    }

    @Test
    void 베스트셀러_조회_판매량_랭킹() throws Exception {
        mockMvc.perform(get("/books/bestsellers").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book1.getId())) // 2 orders
                .andExpect(jsonPath("$.data.content[1].id", in(
                        List.of(book3.getId().intValue(), book4.getId().intValue()) // 3 혹은 4
                )));
    }

    @Test
    void 삼십일간_리뷰_많은_책_조회() throws Exception {
        mockMvc.perform(get("/books/top-reviews-30days").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book1.getId())) // 2 reviews
                .andExpect(jsonPath("$.data.content[1].id").value(book3.getId())); // 1 review
    }

    @Test
    void 조회수_랭킹_조회() throws Exception {
        mockMvc.perform(get("/books/ranking").param("type", "VIEWS").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book3.getId())) // 500 views
                .andExpect(jsonPath("$.data.content[1].id").value(book4.getId())); // 200 views
    }

    @Test
    void 판매량_랭킹_조회() throws Exception {
        mockMvc.perform(get("/books/ranking").param("type", "SALES").param("size", "4"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.data.content[0].id").value(book1.getId())) // 2 orders
                .andExpect(jsonPath("$.data.content[1].id").value(book3.getId()))
                .andExpect(jsonPath("$.data.content[2].id").value(book4.getId()))
                .andExpect(jsonPath("$.data.content[3].id").value(book2.getId())); // 0 order
    }

    @Test
    void 얼리어답터의_픽_조회() throws Exception {
        mockMvc.perform(get("/books/early-adopter-picks").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value(book1.getId())) // ordered by 2 members
                .andExpect(jsonPath("$.data.content[1].id").value(book3.getId()));// ordered by 1 member
    }

    @Test
    void 책메이트_추천_조회_재고있는_책_중_랜덤() throws Exception {
        mockMvc.perform(get("/books/chaekmate-picks").param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(3)); // book1, book3, book4 are in stock
    }
}
