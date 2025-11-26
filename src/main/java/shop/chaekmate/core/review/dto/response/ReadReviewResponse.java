package shop.chaekmate.core.review.dto.response;

import java.time.LocalDateTime;

public record ReadReviewResponse(
        Long id,
        Long memberId,
        Long orderedBookId,
        String comment,
        Integer rating,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
