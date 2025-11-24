package shop.chaekmate.core.order.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "비회원 주문 조회 요청")
public record NonMemberOrderHistoryRequest(
        @Schema(description = "주문번호", example = "ROEP30FEi7hi5yR8WK4pd")
        String orderNumber,
        @Schema(description = "주문자명", example = "홍길동")
        String ordererName,
        @Schema(description = "주문자 휴대폰 번호", example = "010-1234-5678")
        String ordererPhone
) {
}
