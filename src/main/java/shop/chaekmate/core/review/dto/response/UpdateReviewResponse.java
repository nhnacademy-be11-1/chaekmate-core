package shop.chaekmate.core.review.dto.response;

public record UpdateReviewResponse(
        Long id,
        Long memberId,
        Long orderedBookId,
        String comment,
        Integer rating
) {
}

