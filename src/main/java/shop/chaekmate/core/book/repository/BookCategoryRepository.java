package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookCategory;

import java.util.List;
import java.util.Set;

public interface BookCategoryRepository extends JpaRepository<BookCategory, Long> {
    @Modifying
    void deleteByBookIdAndCategoryIdIn(Long id, Set<Long> idsToRemove);

    List<BookCategory> findByBook(Book book);
}
