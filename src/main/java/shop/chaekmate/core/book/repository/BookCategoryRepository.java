package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.BookCategory;
import shop.chaekmate.core.book.entity.Category;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    boolean existsByCategory(Category category);

}
