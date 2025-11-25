package shop.chaekmate.core.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;

public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long> {
    List<OrderedBook> findByOrder(Order order);
}
