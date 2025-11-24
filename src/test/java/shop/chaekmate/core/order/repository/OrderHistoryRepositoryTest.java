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
import shop.chaekmate.core.order.dto.request.NonMemberOrderHistoryRequest;
import shop.chaekmate.core.order.entity.Order;

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

    private Member member1;
    private Order order1;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(new Member("id1", "pwd", "tester", "010-1234-5678", "test@email.com", LocalDate.now(), PlatformType.LOCAL));
        bookRepository.save(Book.builder().title("A Book").isbn("1").author("a").description("d").index("i").isSaleEnd(false).isWrappable(true).price(1000).salesPrice(900).stock(1).views(1).publisher("p").publishedAt(
                LocalDateTime.now()).build());
        order1 = orderRepository.save(Order.createOrderReady(member1, "order123", "tester", "010-1234-5678", "test@email.com", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 10000));
    }

    @Test
    void 비회원_주문_조회_성공_모든_조건() {
        // given
        NonMemberOrderHistoryRequest request = new NonMemberOrderHistoryRequest("order123", "tester", "010-1234-5678");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(order1.getId());
    }

    @Test
    void 비회원_주문_조회_성공_일부_조건() {
        // given
        NonMemberOrderHistoryRequest request = new NonMemberOrderHistoryRequest(null, "tester", "010-1234-5678");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(request, pageable);

        // then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(order1.getId());
    }

    @Test
    void 비회원_주문_조회_실패_조건_불일치() {
        // given
        NonMemberOrderHistoryRequest request = new NonMemberOrderHistoryRequest("wrong-number", "tester", "010-1234-5678");
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(request, pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void 비회원_주문_조회_실패_조건_없음() {
        // given
        NonMemberOrderHistoryRequest request = new NonMemberOrderHistoryRequest(null, null, null);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.searchNonMemberOrder(request, pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
    }
}
