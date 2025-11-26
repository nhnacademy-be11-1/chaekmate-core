package shop.chaekmate.core.review.dto.response;

import lombok.Builder;

@Builder
public record ReviewImageResponse(
    Long reviewImageId,
    String imageUrl
) {
}
