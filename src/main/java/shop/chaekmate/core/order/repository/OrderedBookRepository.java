package shop.chaekmate.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.OrderedBook;

public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long> {
}
