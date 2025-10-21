package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
