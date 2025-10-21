package shop.chaekmate.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
