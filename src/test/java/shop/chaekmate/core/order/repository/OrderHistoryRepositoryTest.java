package shop.chaekmate.core.order.repository;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Import({QueryDslConfig.class, JpaAuditingConfig.class, OrderRepositoryImpl.class})
class OrderHistoryRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OrderedBookRepository orderedBookRepository;

    private Member member1;
    private Order order1, order2;
    private Book book1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(new Member("id1", "pwd", "tester", "010-1234-5678", "test@email.com", LocalDate.now(), PlatformType.LOCAL));
        book1 = bookRepository.save(Book.builder().title("A Book").isbn("1").author("a").description("d").index("i").isSaleEnd(false).isWrappable(true).price(1000).salesPrice(900).stock(1).views(1).publisher("p").publishedAt(
                LocalDateTime.now()).build());

        order1 = orderRepository.save(Order.createOrderReady(member1, "order123", "tester", "010-1234-5678", "test@email.com", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 10000));
        OrderedBook ob1 = OrderedBook.createOrderDetailReady(order1, book1, 1, 1000, 900, 100, null, 0, null, 0, 0, 900);
        ob1.markPaymentCompleted();
        orderedBookRepository.save(ob1);

        order2 = orderRepository.save(Order.createOrderReady(member1, "order456", "tester2", "010-4567-8901", "test2@email.com", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 20000));
        OrderedBook ob2 = OrderedBook.createOrderDetailReady(order2, book1, 1, 1000, 900, 100, null, 0, null, 0, 0, 900);
        orderedBookRepository.save(ob2); // Stays as PAYMENT_READY
    }

    @Test
    void 회원_주문_조회_성공() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.findMemberOrders(member1.getId(), pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(order1.getId());
    }

    @Test
    void 비회원_주문_조회_성공_모든_조건() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder("order123", "tester", "010-1234-5678", pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(order1.getId());
    }

    @Test
    void 비회원_주문_조회시_잘못된_상태의_주문은_필터링된다() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder("order456", "tester2", "010-4567-8901", pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void 비회원_주문_조회_성공_일부_조건() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(null, "tester", "010-1234-5678", pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(order1.getId());
    }

    @Test
    void 비회원_주문_조회_실패_조건_불일치() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder("wrong-number", "tester", "010-1234-5678", pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void 비회원_주문_조회_실패_조건_없음() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(null, null, null, pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
    }
}
