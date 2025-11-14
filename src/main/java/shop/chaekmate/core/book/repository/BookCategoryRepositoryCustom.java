package shop.chaekmate.core.book.repository;

import java.util.List;

public interface BookCategoryRepositoryCustom {
    List<String> findCategoryNamesByBookId(Long bookId);
}
