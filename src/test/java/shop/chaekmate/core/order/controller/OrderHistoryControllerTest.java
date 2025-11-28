package shop.chaekmate.core.order.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import shop.chaekmate.core.order.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderedBookRepository orderedBookRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private Member member;
    private Order order;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(new Member("id1", "pwd", "tester", "010-1234-5678", "test@email.com", LocalDate.now(), PlatformType.LOCAL));
        Book book = bookRepository.save(Book.builder().title("A Book").isbn("1").author("a").description("d").index("i").isSaleEnd(false).isWrappable(true).price(1000).salesPrice(900).stock(1).views(1).publisher("p").publishedAt(LocalDateTime.now()).build());
        order = orderRepository.save(Order.createOrderReady(member, "order123", "tester", "010-1234-5678", "test@email.com", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 10000));

        OrderedBook orderedBook = OrderedBook.createOrderDetailReady(order, book, 1, 1000, 900, 100, null, 0, null, 0, 0, 900,900);
        orderedBook.markPaymentCompleted();
        orderedBookRepository.save(orderedBook);
    }

    @Test
    void 회원_주문_내역_조회_성공() throws Exception {
        mockMvc.perform(get("/orders/history/member")
                        .header("X-Member-Id", member.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderId").value(order.getId()));
    }

    @Test
    void 비회원_주문_내역_조회_성공() throws Exception {
        mockMvc.perform(get("/orders/history/non-member")
                        .param("orderNumber", "order123")
                        .param("ordererName", "tester")
                        .param("ordererPhone", "010-1234-5678")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderId").value(order.getId()));
    }

    @Test
    void 비회원_주문_내역_조회_실패_내역_없음() throws Exception {
        mockMvc.perform(get("/orders/history/non-member")
                        .param("orderNumber", "wrong-number")
                        .param("ordererName", "tester")
                        .param("ordererPhone", "010-1234-5678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    void 비회원_주문_내역_조회_성공_주문번호만_입력() throws Exception {
        mockMvc.perform(get("/orders/history/non-member")
                        .param("orderNumber", "order123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderId").value(order.getId()));
    }

    @Test
    void 비회원_주문_내역_조회_성공_주문자명만_입력() throws Exception {
        mockMvc.perform(get("/orders/history/non-member")
                        .param("ordererName", "tester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].orderId").value(order.getId()));
    }

    @Test
    void 비회원_주문_내역_조회_실패_파라미터_없음() throws Exception {
        mockMvc.perform(get("/orders/history/non-member"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }
}
