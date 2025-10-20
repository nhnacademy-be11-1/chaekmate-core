package shop.chaekmate.core.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
