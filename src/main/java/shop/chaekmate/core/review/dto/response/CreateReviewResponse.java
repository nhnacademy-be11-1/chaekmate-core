package shop.chaekmate.core.review.dto.response;

public record CreateReviewResponse(
        Long id,
        Long memberId,
        Long orderedBookId,
        String comment,
        Integer rating
) {
}
