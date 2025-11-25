package shop.chaekmate.core.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.review.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
}
