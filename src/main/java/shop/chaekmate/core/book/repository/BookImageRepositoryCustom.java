package shop.chaekmate.core.book.repository;

import java.util.List;
import shop.chaekmate.core.book.entity.BookImage;

public interface BookImageRepositoryCustom {
    List<BookImage> findAllByBookIdOrderByCreatedAtAsc(Long bookId);
}
