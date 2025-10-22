package shop.chaekmate.core.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByNameAndDeletedAtNull(String name);

    boolean existsByIdAndDeletedAtNotNull(Long id);

    boolean existsByIdAndDeletedAtNull(Long id);

    Tag findByIdAndDeletedAtNull(Long id);


    List<Tag> findByDeletedAtNull();

}
