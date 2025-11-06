package shop.chaekmate.core.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.response.BookListResponse;

public interface BookRepositoryCustom {
    Page<BookListResponse> searchBooks(BookSearchCondition condition, Pageable pageable);
}
