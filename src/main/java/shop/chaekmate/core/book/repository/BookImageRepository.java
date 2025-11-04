package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.BookImage;

import java.util.Optional;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {
    Optional<BookImage> findByBookId(Long bookId);
}
