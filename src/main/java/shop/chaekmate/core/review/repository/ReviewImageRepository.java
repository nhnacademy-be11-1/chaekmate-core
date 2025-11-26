package shop.chaekmate.core.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.review.entity.ReviewImage;

import java.util.List;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Long> {
    List<ReviewImage> findByReviewId(Long reviewId);

    long deleteByReview(Review review);
}
