package shop.chaekmate.core.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.order.dto.request.NonMemberOrderHistoryRequest;
import shop.chaekmate.core.order.entity.Order;

public interface OrderRepositoryCustom {
    Page<Order> searchNonMemberOrder(NonMemberOrderHistoryRequest request, Pageable pageable);
}
