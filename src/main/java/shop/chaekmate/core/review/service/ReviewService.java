package shop.chaekmate.core.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.review.dto.request.CreateReviewRequest;
import shop.chaekmate.core.review.dto.request.UpdateReviewRequest;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;
import shop.chaekmate.core.review.dto.response.ReadReviewResponse;
import shop.chaekmate.core.review.dto.response.UpdateReviewResponse;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.review.exception.MemberNotFoundException;
import shop.chaekmate.core.review.exception.OrderedBookNotFoundException;
import shop.chaekmate.core.review.exception.ReviewNotFoundException;
import shop.chaekmate.core.review.repository.ReviewRepository;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final OrderedBookRepository orderedBookRepository;

    //Review 생성 기능
    @Transactional
    public CreateReviewResponse createReview(CreateReviewRequest request) {
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(MemberNotFoundException::new);

        OrderedBook orderedBook = orderedBookRepository.findById(request.orderedBookId())
                .orElseThrow(OrderedBookNotFoundException::new);

        Review review = Review.createReview(
                member,
                orderedBook,
                request.comment(),
                request.rating()
        );

        Review saved = reviewRepository.save(review);

        return new CreateReviewResponse(
                saved.getId(),
                saved.getMember().getId(),
                saved.getOrderedBook().getId(),
                saved.getComment(),
                saved.getRating()
        );
    }

    //Review 단일 조회 기능
    @Transactional(readOnly = true)
    public ReadReviewResponse readReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        return new ReadReviewResponse(
                review.getId(),
                review.getMember().getId(),
                review.getOrderedBook().getId(),
                review.getComment(),
                review.getRating(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }

    //Review 페이지네이션 조회 기능
    @Transactional(readOnly = true)
    public Page<ReadReviewResponse> readAllReviewsByPage(Pageable pageable) {
        return reviewRepository.findAll(pageable)
                .map(review -> new ReadReviewResponse(
                        review.getId(),
                        review.getMember().getId(),
                        review.getOrderedBook().getId(),
                        review.getComment(),
                        review.getRating(),
                        review.getCreatedAt(),
                        review.getUpdatedAt()
                ));
    }

    //Review 수정 기능
    @Transactional
    public UpdateReviewResponse updateReview(Long reviewId, UpdateReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);

        review.updateReview(request.comment(), request.rating());
        reviewRepository.save(review);

        return new UpdateReviewResponse(
                review.getId(),
                review.getMember().getId(),
                review.getOrderedBook().getId(),
                review.getComment(),
                review.getRating()
        );
    }

    //Review 삭제 기능
    @Transactional
    public void deleteReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(ReviewNotFoundException::new);
        reviewRepository.delete(review);
    }

}
