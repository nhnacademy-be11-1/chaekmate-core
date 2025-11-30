package shop.chaekmate.core.review.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.review.dto.request.ReviewImageAddRequest;
import shop.chaekmate.core.review.dto.response.ReviewImageResponse;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.review.entity.ReviewImage;
import shop.chaekmate.core.review.event.ReviewEventPublisher;
import shop.chaekmate.core.review.exception.ReviewNotFoundException;
import shop.chaekmate.core.review.repository.ReviewImageRepository;
import shop.chaekmate.core.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewImageService {

    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final ReviewEventPublisher reviewEventPublisher;

    @Transactional
    public List<ReviewImageResponse> addImages(Long reviewId, ReviewImageAddRequest request) {
        Review review = findReviewById(reviewId);
        
        List<ReviewImage> existingImages = reviewImageRepository.findByReviewId(reviewId);
        int existingImageCount = existingImages.size();
        
        List<ReviewImage> reviewImages = request.imageUrls().stream()
                .map(imageUrl -> new ReviewImage(review, imageUrl))
                .toList();
        reviewImageRepository.saveAll(reviewImages);
        if (!reviewImages.isEmpty() 
                && existingImageCount == 0 
                && review.getComment() != null 
                && !review.getComment().trim().isEmpty()) {
            reviewEventPublisher.publishReviewCreated(reviewId, review.getMember().getId());
        }
        
        return reviewImages.stream()
                .map(image -> new ReviewImageResponse(image.getId(), image.getImageUrl()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReviewImageResponse> findImages(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException();
        }
        return reviewImageRepository.findByReviewId(reviewId).stream()
                .map(image -> new ReviewImageResponse(image.getId(), image.getImageUrl()))
                .toList();
    }

    @Transactional
    public void deleteImage(Long reviewId, Long imageId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException();
        }
        reviewImageRepository.deleteById(imageId);
    }

    private Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
    }
}
