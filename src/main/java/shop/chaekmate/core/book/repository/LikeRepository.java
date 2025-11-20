package shop.chaekmate.core.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {
    List<Like> findByBook_Id(Long id);

    List<Like> findByMember_Id(Long id);

    List<Like> findAllByBook_IdAndMember_Id(Long id, Long id1);

}
