package shop.chaekmate.core.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
