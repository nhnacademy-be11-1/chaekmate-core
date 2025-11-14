package shop.chaekmate.core.book.repository;

import java.util.List;

public interface BookTagRepositoryCustom {
    List<String> findTagNamesByBookId(Long bookId);
}
