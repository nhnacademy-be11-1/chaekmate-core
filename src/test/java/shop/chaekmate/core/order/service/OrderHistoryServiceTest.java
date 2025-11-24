package shop.chaekmate.core.order.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class OrderHistoryServiceTest {

    @InjectMocks
    private OrderHistoryService orderHistoryService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderedBookRepository orderedBookRepository;

    @Nested
    @DisplayName("주문 내역 조회")
    class FindOrderHistory {

        private Order createMockOrder(Long id) {
            Order order = mock(Order.class);
            when(order.getId()).thenReturn(id);
            when(order.getOrderNumber()).thenReturn("ORDER" + id);
            when(order.getCreatedAt()).thenReturn(LocalDateTime.now());
            when(order.getOrdererName()).thenReturn("Name" + id);
            when(order.getOrdererPhone()).thenReturn("Phone" + id);
            when(order.getOrdererEmail()).thenReturn("Email" + id);
            when(order.getRecipientName()).thenReturn("RName" + id);
            when(order.getRecipientPhone()).thenReturn("RPhone" + id);
            when(order.getZipcode()).thenReturn("Zip" + id);
            when(order.getStreetName()).thenReturn("Street" + id);
            when(order.getDetail()).thenReturn("Detail" + id);
            when(order.getDeliveryRequest()).thenReturn("Request" + id);
            when(order.getDeliveryAt()).thenReturn(LocalDateTime.now().toLocalDate());
            when(order.getDeliveryFee()).thenReturn(2500);
            when(order.getTotalPrice()).thenReturn(10000L);
            when(order.getStatus()).thenReturn(shop.chaekmate.core.order.entity.type.OrderStatusType.PAYMENT_READY);
            return order;
        }

        private OrderedBook createMockOrderedBook(Order order, Long bookId, String bookTitle) {
            OrderedBook orderedBook = mock(OrderedBook.class);
            when(orderedBook.getOrder()).thenReturn(order);
            Book book = mock(Book.class);
            when(book.getId()).thenReturn(bookId);
            when(book.getTitle()).thenReturn(bookTitle);
            when(orderedBook.getBook()).thenReturn(book);
            when(orderedBook.getQuantity()).thenReturn(1);
            when(orderedBook.getFinalUnitPrice()).thenReturn(100);
            when(orderedBook.getTotalPrice()).thenReturn(120L);
            when(orderedBook.getUnitStatus()).thenReturn(OrderedBookStatusType.PAYMENT_COMPLETE);
            return orderedBook;
        }

        @Test
        void 회원_주문_내역_조회_성공() {
            // given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            Order mockOrder1 = createMockOrder(1L);
            Order mockOrder2 = createMockOrder(2L);
            Page<Order> orderPage = new PageImpl<>(List.of(mockOrder1, mockOrder2), pageable, 2);

            OrderedBook mockOb1 = createMockOrderedBook(mockOrder1, 101L, "Book A");
            OrderedBook mockOb2 = createMockOrderedBook(mockOrder1, 102L, "Book B");
            OrderedBook mockOb3 = createMockOrderedBook(mockOrder2, 103L, "Book C");

            when(orderRepository.findByMemberId(memberId, pageable)).thenReturn(orderPage);
            when(orderedBookRepository.findAllByOrderIn(any())).thenReturn(List.of(mockOb1, mockOb2, mockOb3));

            // when
            Page<OrderHistoryResponse> result = orderHistoryService.findMemberOrderHistory(memberId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(2);
            assertThat(result.getContent().get(0).orderedBooks()).hasSize(2);
            assertThat(result.getContent().get(1).orderedBooks()).hasSize(1);
        }

        @Test
        void 비회원_주문_내역_조회_성공() {
            // given
            String orderNumber = "1";
            String ordererName = "n";
            String ordererPhone = "p";
            Pageable pageable = PageRequest.of(0, 10);
            Order mockOrder1 = createMockOrder(1L);
            Page<Order> orderPage = new PageImpl<>(List.of(mockOrder1), pageable, 1);

            OrderedBook mockOb1 = createMockOrderedBook(mockOrder1, 101L, "Book A");

            when(orderRepository.searchNonMemberOrder(orderNumber, ordererName, ordererPhone, pageable)).thenReturn(orderPage);
            when(orderedBookRepository.findAllByOrderIn(any())).thenReturn(List.of(mockOb1));

            // when
            Page<OrderHistoryResponse> result = orderHistoryService.findNonMemberOrderHistory(orderNumber, ordererName, ordererPhone, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().orderedBooks()).hasSize(1);
        }

        @Test
        void 주문_내역이_없을_경우_빈_페이지_반환() {
            // given
            Long memberId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            when(orderRepository.findByMemberId(memberId, pageable)).thenReturn(Page.empty());

            // when
            Page<OrderHistoryResponse> result = orderHistoryService.findMemberOrderHistory(memberId, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getTotalElements()).isZero();
            assertThat(result.getContent()).isEmpty();
        }
    }
}
