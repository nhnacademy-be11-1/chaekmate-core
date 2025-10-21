package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.BookCategory;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
}
