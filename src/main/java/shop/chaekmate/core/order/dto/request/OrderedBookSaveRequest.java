package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "주문 상품 저장 요청")
public record OrderedBookSaveRequest(

        @Schema(description = "도서 ID", example = "156919")
        @NotNull(message = "bookId는 필수입니다.")
        Long bookId,

        @Schema(description = "수량", example = "1")
        @Positive(message = "수량은 1 이상이어야 합니다.")
        int quantity,

        @Schema(description = "정가", example = "20000")
        @Positive(message = "원가는 0보다 커야 합니다.")
        int originalPrice,

        @Schema(description = "판매가(할인가 적용)", example = "17500")
        Integer salesPrice,

        @Schema(description = "할인 금액", example = "2500")
        Integer discountPrice,

        @Schema(description = "포장지 ID (없으면 null)", example = "1")
        Long wrapperId,

        @Schema(description = "포장지 가격", example = "1000")
        Integer wrapperPrice,

        @Schema(description = "사용한 쿠폰 ID", example = "1")
        Long issuedCouponId,

        @Schema(description = "쿠폰 할인 금액", example = "3000")
        Integer couponDiscount,

        @Schema(description = "사용 포인트 금액", example = "500")
        Integer pointUsed,

        @Schema(description = "이 상품의 최종 단가", example = "10500")
        @Positive(message = "최종 단가는 0보다 커야 합니다.")
        int finalUnitPrice

) { }
