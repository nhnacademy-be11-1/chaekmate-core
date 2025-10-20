package shop.chaekmate.core.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.cart.entity.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
