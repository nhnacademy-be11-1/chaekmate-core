package shop.chaekmate.core.order.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;

public interface OrderedBookRepository extends JpaRepository<OrderedBook, Long>, OrderedBookRepositoryCustom {
    List<OrderedBook> findAllByOrderIn(List<Order> orders);

}
