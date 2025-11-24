package shop.chaekmate.core.order.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findByOrderNumber(String orderNumber);
    boolean existsByOrderNumber(String orderNumber);

    Optional<Order> findByOrderNumberAndOrdererNameAndOrdererPhone(String orderNumber, String ordererName, String ordererPhone);
}
