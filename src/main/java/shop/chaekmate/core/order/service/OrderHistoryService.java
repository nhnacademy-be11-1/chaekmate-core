package shop.chaekmate.core.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.NonMemberOrderHistoryRequest;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;
import shop.chaekmate.core.order.dto.response.OrderedBookHistoryResponse;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderHistoryService {

    private final OrderRepository orderRepository;
    private final OrderedBookRepository orderedBookRepository;

    public Page<OrderHistoryResponse> findMemberOrderHistory(Long memberId, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByMemberId(memberId, pageable);
        return convertToOrderHistoryResponsePage(ordersPage, pageable);
    }

    public Page<OrderHistoryResponse> findNonMemberOrderHistory(NonMemberOrderHistoryRequest request, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.searchNonMemberOrder(request, pageable);
        return convertToOrderHistoryResponsePage(ordersPage, pageable);
    }

    private Page<OrderHistoryResponse> convertToOrderHistoryResponsePage(Page<Order> ordersPage, Pageable pageable) {
        List<Order> orders = ordersPage.getContent();

        if (orders.isEmpty()) {
            return Page.empty(pageable);
        }

        List<OrderedBook> allOrderedBooks = orderedBookRepository.findAllByOrderIn(orders);

        Map<Long, List<OrderedBookHistoryResponse>> orderedBooksByOrderId = allOrderedBooks.stream()
                .collect(Collectors.groupingBy(
                        orderedBook -> orderedBook.getOrder().getId(),
                        Collectors.mapping(OrderedBookHistoryResponse::from, Collectors.toList())
                ));

        List<OrderHistoryResponse> responses = orders.stream()
                .map(order -> OrderHistoryResponse.of(order, orderedBooksByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();

        return new PageImpl<>(responses, pageable, ordersPage.getTotalElements());
    }
}
