package shop.chaekmate.core.review.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.review.controller.docs.ReviewImageControllerDocs;
import shop.chaekmate.core.review.dto.request.ReviewImageAddRequest;
import shop.chaekmate.core.review.dto.response.ReviewImageResponse;
import shop.chaekmate.core.review.service.ReviewImageService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewImageController implements ReviewImageControllerDocs {

    private final ReviewImageService reviewImageService;

    @Override
    @PostMapping("/reviews/{reviewId}/images")
    public ResponseEntity<List<ReviewImageResponse>> addImages(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewImageAddRequest request) {
        List<ReviewImageResponse> response = reviewImageService.addImages(reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    @GetMapping("/reviews/{reviewId}/images")
    public ResponseEntity<List<ReviewImageResponse>> getImages(
            @PathVariable Long reviewId) {
        List<ReviewImageResponse> response = reviewImageService.findImages(reviewId);
        return ResponseEntity.ok(response);
    }

    @Override
    @DeleteMapping("/reviews/{reviewId}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Long reviewId,
            @PathVariable Long imageId) {
        reviewImageService.deleteImage(reviewId, imageId);
        return ResponseEntity.ok().build();
    }
}
