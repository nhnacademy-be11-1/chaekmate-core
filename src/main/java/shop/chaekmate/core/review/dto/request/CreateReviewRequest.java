package shop.chaekmate.core.review.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReviewRequest(
        @NotNull(message = "회원 ID는 필수입니다.")
        Long memberId,

        @NotNull(message = "주문 도서 ID는 필수입니다.")
        Long orderedBookId,

        @NotBlank(message = "리뷰 내용은 필수입니다.")
        String comment,

        @NotNull(message = "별점은 필수입니다.")
        @Min(value = 1, message = "별점은 최소 1점이어야 합니다.")
        @Max(value = 5, message = "별점은 최대 5점이어야 합니다.")
        Integer rating
) {
}
