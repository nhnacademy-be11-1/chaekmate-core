package shop.chaekmate.core.order.repository;

import java.util.List;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;

public interface OrderedBookRepositoryCustom {
    List<OrderedBook> findAllByOrder(Order order);
    List<OrderedBook> findShippingBooks();
    boolean isAllBooksDelivered(Long orderId);
}
