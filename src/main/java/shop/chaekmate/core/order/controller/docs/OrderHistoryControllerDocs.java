package shop.chaekmate.core.order.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;

@Tag(name = "주문 내역 조회 API", description = "회원 및 비회원의 주문 내역 조회 관련 API")
public interface OrderHistoryControllerDocs {

    @Operation(summary = "회원 주문 내역 조회", description = "X-Member-Id 헤더의 회원 ID를 기반으로 주문 내역 목록을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<Page<OrderHistoryResponse>> getMemberOrderHistory(
            @Parameter(description = "요청 사용자 ID", required = true, example = "1") @RequestHeader("X-Member-Id") Long memberId,
            @ParameterObject Pageable pageable);

    @Operation(summary = "비회원 주문 내역 조회", description = "주문번호, 주문자 이름, 주문자 연락처로 주문 내역을 페이징하여 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<Page<OrderHistoryResponse>> getNonMemberOrderHistory(
            @Parameter(description = "주문번호", required = false, example = "ROEP30FEi7hi5yR8WK4pd") @RequestParam String orderNumber,
            @Parameter(description = "주문자명", required = false, example = "홍길동") @RequestParam String ordererName,
            @Parameter(description = "주문자 휴대폰 번호", required = false, example = "010-1234-5678") @RequestParam String ordererPhone,
            @ParameterObject Pageable pageable);
}
