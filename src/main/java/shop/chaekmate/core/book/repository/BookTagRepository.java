package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.BookTag;

public interface BookTagRepository extends JpaRepository<BookTag, Long> {
}
