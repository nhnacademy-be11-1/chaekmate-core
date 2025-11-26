package shop.chaekmate.core.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.book.dto.response.PageResponse;
import shop.chaekmate.core.review.controller.docs.ReviewControllerDocs;
import shop.chaekmate.core.review.dto.request.CreateReviewRequest;
import shop.chaekmate.core.review.dto.request.UpdateReviewRequest;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;
import shop.chaekmate.core.review.dto.response.ReadReviewResponse;
import shop.chaekmate.core.review.dto.response.UpdateReviewResponse;
import shop.chaekmate.core.review.service.ReviewService;

@RestController
@RequiredArgsConstructor
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;

    @PostMapping("/reviews")
    public ResponseEntity<CreateReviewResponse> createReview(
            @Valid @RequestBody CreateReviewRequest request
    ) {
        CreateReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/reviews/{id}")
    public ResponseEntity<ReadReviewResponse> readReview(
            @PathVariable(name = "id") Long reviewId
    ) {
        ReadReviewResponse response = reviewService.readReviewById(reviewId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<UpdateReviewResponse> updateReview(
            @PathVariable(name = "id") Long reviewId,
            @Valid @RequestBody UpdateReviewRequest request
    ) {
        UpdateReviewResponse response = reviewService.updateReview(reviewId, request);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<PageResponse<ReadReviewResponse>> readReviewsByBookId(
            @PathVariable("bookId") Long bookId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(PageResponse.from(reviewService.readReviewsByBookId(bookId, pageable)));
    }


    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable(name = "id") Long reviewId
    ) {
        reviewService.deleteReviewById(reviewId);
        return ResponseEntity.noContent().build();
    }
}

