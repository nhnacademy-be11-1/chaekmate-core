package shop.chaekmate.core.book.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);

}
