package shop.chaekmate.core.review.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.review.dto.request.ReviewImageAddRequest;
import shop.chaekmate.core.review.dto.response.ReviewImageResponse;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.review.entity.ReviewImage;
import shop.chaekmate.core.review.exception.ReviewNotFoundException;
import shop.chaekmate.core.review.repository.ReviewImageRepository;
import shop.chaekmate.core.review.repository.ReviewRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ReviewImageServiceTest {

    @InjectMocks
    private ReviewImageService reviewImageService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewImageRepository reviewImageRepository;

    @Test
    void 이미지_추가_성공() {
        // given
        long reviewId = 1L;
        Review review = mock(Review.class);
        ReviewImageAddRequest request = new ReviewImageAddRequest(List.of("url1", "url2"));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // when
        List<ReviewImageResponse> responses = reviewImageService.addImages(reviewId, request);

        // then
        assertAll(
                () -> assertThat(responses).hasSize(2),
                () -> verify(reviewImageRepository, times(1)).saveAll(any())
        );
    }

    @Test
    void 이미지_추가_실패_리뷰_없음() {
        // given
        long reviewId = 1L;
        ReviewImageAddRequest request = new ReviewImageAddRequest(List.of("url1"));
        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> reviewImageService.addImages(reviewId, request));
    }

    @Test
    void 이미지_조회_성공() {
        // given
        long reviewId = 1L;
        Review review = mock(Review.class);
        List<ReviewImage> images = List.of(
                new ReviewImage(review, "url1"),
                new ReviewImage(review, "url2")
        );
        when(reviewRepository.existsById(reviewId)).thenReturn(true);
        when(reviewImageRepository.findByReviewId(reviewId)).thenReturn(images);

        // when
        List<ReviewImageResponse> responses = reviewImageService.findImages(reviewId);

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    void 이미지_조회_실패_리뷰_없음() {
        // given
        long reviewId = 1L;
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> reviewImageService.findImages(reviewId));
    }

    @Test
    void 이미지_삭제_성공() {
        // given
        long reviewId = 1L;
        long imageId = 10L;
        when(reviewRepository.existsById(reviewId)).thenReturn(true);

        // when
        reviewImageService.deleteImage(reviewId, imageId);

        // then
        verify(reviewImageRepository, times(1)).deleteById(imageId);
    }

    @Test
    void 이미지_삭제_실패_리뷰_없음() {
        // given
        long reviewId = 1L;
        long imageId = 10L;
        when(reviewRepository.existsById(reviewId)).thenReturn(false);

        // when & then
        assertThrows(ReviewNotFoundException.class, () -> reviewImageService.deleteImage(reviewId, imageId));
    }
}
