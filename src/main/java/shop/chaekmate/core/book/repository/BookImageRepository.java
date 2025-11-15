package shop.chaekmate.core.book.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;

public interface BookImageRepository extends JpaRepository<BookImage, Long>, BookImageRepositoryCustom {
    List<BookImage> findByBookId(Long bookId);

    List<BookImage> findByBook(Book book);

}
