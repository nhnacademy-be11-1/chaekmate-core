package shop.chaekmate.core.book.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookQueryResponse;

public interface BookRepositoryCustom {
    Page<BookListResponse> searchBooks(BookSearchCondition condition, Pageable pageable);

    Slice<BookQueryResponse> findRecentlyAddedBooks(Pageable pageable);

    Slice<BookQueryResponse> findNewBooks(Pageable pageable);

    Slice<BookQueryResponse> findAllBooks(BookSearchCondition condition, Pageable pageable);

    Slice<BookQueryResponse> findBestsellers(Pageable pageable);

    Slice<BookQueryResponse> findTopReviewedBooksForLast30Days(Pageable pageable);

    Slice<BookQueryResponse> findRandomInStockBooks(Pageable pageable);

    Slice<BookQueryResponse> findBooksByViews(Pageable pageable);

    Slice<BookQueryResponse> findEarlyAdopterPicks(Pageable pageable);
}
