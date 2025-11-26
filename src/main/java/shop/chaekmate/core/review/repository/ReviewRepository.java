package shop.chaekmate.core.review.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.review.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByOrderedBook_Book_Id(Long bookId, Pageable pageable);
}
