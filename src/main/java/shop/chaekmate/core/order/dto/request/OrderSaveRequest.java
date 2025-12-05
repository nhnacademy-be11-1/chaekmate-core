package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "주문 저장 요청")
public record OrderSaveRequest(

        @Schema(description = "주문자 이름", example = "홍길동")
        @NotBlank(message = "주문자 이름은 필수입니다.")
        String ordererName,

        @Schema(description = "주문자 연락처", example = "01012345678")
        @NotBlank(message = "주문자 전화번호 필수입니다.")
        String ordererPhone,

        @Schema(description = "주문자 이메일", example = "test@example.com")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotBlank(message = "주문자 이메일은 필수입니다.")
        String ordererEmail,

        @Schema(description = "수령인 이름", example = "홍길동")
        @NotBlank(message = "수령자 이름은 필수입니다.")
        String recipientName,

        @Schema(description = "수령인 연락처", example = "01098765432")
        @NotBlank(message = "수령자 전화번호는 필수입니다.")
        String recipientPhone,

        @Schema(description = "우편번호(5자리)", example = "12345")
        @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다.")
        @NotBlank(message = "우편번호는 필수입니다.")
        String zipcode,

        @Schema(description = "도로명 주소", example = "서울특별시 OO구 OO로 123")
        @NotBlank(message = "도로명 주소는 필수입니다.")
        String streetName,

        @Schema(description = "상세 주소", example = "101동 202호")
        @NotNull(message = "상세주소는 필수입니다.")
        String detail,

        @Schema(description = "배송 요청사항", example = "부재 시 문 앞에 놓아주세요")
        String deliveryRequest,

        @Schema(description = "배송 예정일", example = "2025-11-21")
        @NotNull(message = "배송일은 필수입니다.")
        LocalDate deliveryAt,

        @Schema(description = "배송비", example = "3000")
        @PositiveOrZero(message = "배송비는 0 이상이어야 합니다.")
        int deliveryFee,

        @Schema(description = "총 결제 금액", example = "35000")
        @PositiveOrZero(message = "총 결제 금액은 음수일 수 없습니다.")
        long totalPrice,

        @Schema(description = "주문 상품 목록")
        @NotEmpty(message = "주문 상품 목록은 1개 이상이어야 합니다.")
        @Valid
        List<OrderedBookSaveRequest> orderedBooks

) { }
