package shop.chaekmate.core.book.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.RankingType;
import shop.chaekmate.core.book.dto.response.BookQueryResponse;
import shop.chaekmate.core.book.dto.response.BookQuerySliceResponse;
import shop.chaekmate.core.book.repository.BookRepository;

@Service
@RequiredArgsConstructor
public class BookQueryService {

    private final BookRepository bookRepository;

    /**
     * 최근 추가된 도서
     */
    @Cacheable(value = "bookQueries", key = "'findRecentlyAddedBooks_' + #pageable.toString()")
    public BookQuerySliceResponse findRecentlyAddedBooks(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findRecentlyAddedBooks(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 맞춤 추천
     */
    @Cacheable(value = "bookQueries", key = "'findPersonalizedRecommendedBooks_' + #pageable.toString()")
    public BookQuerySliceResponse findPersonalizedRecommendedBooks(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findRandomInStockBooks(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 30일간 리뷰 많은 책
     */
    @Cacheable(value = "bookQueries", key = "'findTopReviewedBooksForLast30Days_' + #pageable.toString()")
    public BookQuerySliceResponse findTopReviewedBooksForLast30Days(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findTopReviewedBooksForLast30Days(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 얼리어답터들의 픽
     */
    @Cacheable(value = "bookQueries", key = "'findEarlyAdopterPicks_' + #pageable.toString()")
    public BookQuerySliceResponse findEarlyAdopterPicks(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findEarlyAdopterPicks(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 책메이트 추천
     */
    @Cacheable(value = "bookQueries", key = "'findChaekmateRecommendedBooks_' + #pageable.toString()")
    public BookQuerySliceResponse findChaekmateRecommendedBooks(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findRandomInStockBooks(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 신간도서
     */
    @Cacheable(value = "bookQueries", key = "'findNewBooks_' + #pageable.toString()")
    public BookQuerySliceResponse findNewBooks(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findNewBooks(pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 전체도서
     */
    @Cacheable(value = "bookQueries", key = "'findAllBooks_' + #condition.toString() + '_' + #pageable.toString()")
    public BookQuerySliceResponse findAllBooks(BookSearchCondition condition, Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findAllBooks(condition, pageable);
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 랭킹
     */
    @Cacheable(value = "bookQueries", key = "'findBookRanking_' + #type.name() + '_' + #pageable.toString()")
    public BookQuerySliceResponse findBookRanking(RankingType type, Pageable pageable) {
        Slice<BookQueryResponse> slice = switch (type) {
            case VIEWS -> bookRepository.findBooksByViews(pageable);
            case SALES -> bookRepository.findBestsellers(pageable);
        };
        return BookQuerySliceResponse.from(slice);
    }

    /**
     * 베스트셀러
     */
    @Cacheable(value = "bookQueries", key = "'findBestsellers_' + #pageable.toString()")
    public BookQuerySliceResponse findBestsellers(Pageable pageable) {
        Slice<BookQueryResponse> slice = bookRepository.findBestsellers(pageable);
        return BookQuerySliceResponse.from(slice);
    }
}
