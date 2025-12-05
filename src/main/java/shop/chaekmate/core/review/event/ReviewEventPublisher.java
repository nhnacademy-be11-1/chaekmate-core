package shop.chaekmate.core.review.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;

@Component
@RequiredArgsConstructor
public class ReviewEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishReviewCreated(Long reviewId, Long memberId) {
        publisher.publishEvent(new ReviewCreatedEvent(reviewId, memberId));
    }

    public void publishTextReviewCreated(CreateReviewResponse response) {
        publisher.publishEvent(new TextReviewCreatedEvent(response));
    }
}

