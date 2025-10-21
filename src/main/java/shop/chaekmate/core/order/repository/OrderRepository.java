package shop.chaekmate.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
