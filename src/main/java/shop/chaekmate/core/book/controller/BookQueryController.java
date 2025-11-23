package shop.chaekmate.core.book.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.controller.docs.BookQueryControllerDocs;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.RankingType;
import shop.chaekmate.core.book.dto.response.BookQuerySliceResponse;
import shop.chaekmate.core.book.service.BookQueryService;

@RestController
@RequiredArgsConstructor
public class BookQueryController implements BookQueryControllerDocs {

    private final BookQueryService bookQueryService;

    @Override
    @GetMapping("/books/recent")
    public ResponseEntity<BookQuerySliceResponse> getRecentlyAddedBooks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findRecentlyAddedBooks(pageable));
    }

    @Override
    @GetMapping("/books/personal-recommendations")
    public ResponseEntity<BookQuerySliceResponse> getPersonalizedRecommendedBooks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findPersonalizedRecommendedBooks(pageable));
    }

    @Override
    @GetMapping("/books/top-reviews-30days")
    public ResponseEntity<BookQuerySliceResponse> getTopReviewedBooksForLast30Days(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findTopReviewedBooksForLast30Days(pageable));
    }

    @Override
    @GetMapping("/books/early-adopter-picks")
    public ResponseEntity<BookQuerySliceResponse> getEarlyAdopterPicks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findEarlyAdopterPicks(pageable));
    }

    @Override
    @GetMapping("/books/chaekmate-picks")
    public ResponseEntity<BookQuerySliceResponse> getChaekmateRecommendedBooks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findChaekmateRecommendedBooks(pageable));
    }

    @Override
    @GetMapping("/books/new-releases")
    public ResponseEntity<BookQuerySliceResponse> getNewBooks(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findNewBooks(pageable));
    }

    @Override
    @GetMapping("/books/all")
    public ResponseEntity<BookQuerySliceResponse> getAllBooks(
            @ParameterObject BookSearchCondition condition,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findAllBooks(condition, pageable));
    }

    @Override
    @GetMapping("/books/ranking")
    public ResponseEntity<BookQuerySliceResponse> getBookRanking(
            @RequestParam RankingType type,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findBookRanking(type, pageable));
    }

    @Override
    @GetMapping("/books/bestsellers")
    public ResponseEntity<BookQuerySliceResponse> getBestsellers(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(bookQueryService.findBestsellers(pageable));
    }
}
