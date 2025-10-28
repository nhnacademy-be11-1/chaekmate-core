package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
