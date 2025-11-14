package shop.chaekmate.core.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookTag;

import java.util.List;
import java.util.Set;

public interface BookTagRepository extends JpaRepository<BookTag, Long>, BookTagRepositoryCustom {
    List<BookTag> findByBook(Book book);

    @Modifying
    void deleteByBookIdAndTagIdIn(Long id, Set<Long> idsToRemove);
}
