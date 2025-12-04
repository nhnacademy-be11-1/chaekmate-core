package shop.chaekmate.core.order.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.chaekmate.core.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {
    Optional<Order> findByOrderNumber(String orderNumber);

    @Query("""
    select distinct o from Order o
    join fetch o.orderedBooks ob
    join fetch ob.book
    where o.orderNumber = :orderNumber
""")
    Optional<Order> findByOrderNumberFetch(String orderNumber);


}
