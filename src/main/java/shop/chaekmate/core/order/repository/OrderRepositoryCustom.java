package shop.chaekmate.core.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.type.OrderStatusType;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;

public interface OrderRepositoryCustom {
    Page<Order> searchNonMemberOrder(String orderNumber, String ordererName, String ordererPhone, Pageable pageable);
    Page<Order> findMemberOrders(Long memberId, Pageable pageable);
    Page<Order> findAllOrders(OrderStatusType orderStatus, OrderedBookStatusType unitStatus, Pageable pageable);
}

