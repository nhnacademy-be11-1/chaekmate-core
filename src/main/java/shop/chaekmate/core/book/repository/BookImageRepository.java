package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.BookImage;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
}
