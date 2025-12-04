package shop.chaekmate.core.review.event;

import shop.chaekmate.core.review.dto.response.CreateReviewResponse;

public record TextReviewCreatedEvent (CreateReviewResponse createReviewResponse) {
}
