package shop.chaekmate.core.order.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;
import shop.chaekmate.core.order.entity.type.OrderStatusType;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;

@Tag(name = "주문 내역 조회 API", description = "회원 및 비회원의 주문 내역 조회 관련 API")
public interface OrderHistoryControllerDocs {

    @Operation(
            summary = "회원 주문 내역 조회",
            description = "X-Member-Id 헤더의 회원 ID를 기반으로 주문 내역 목록을 페이징하여 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<Page<OrderHistoryResponse>> getMemberOrderHistory(
            @Parameter(description = "요청 사용자 ID", example = "1", required = true)
            @RequestHeader("X-Member-Id") Long memberId,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "비회원 주문 내역 조회",
            description = "주문번호, 주문자 이름, 주문자 연락처로 주문 내역을 페이징하여 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<Page<OrderHistoryResponse>> getNonMemberOrderHistory(
            @Parameter(description = "주문번호", example = "ROaEP30FEi7hi5yR8WK4pd")
            @RequestParam(required = false) String orderNumber,
            @Parameter(description = "주문자명", example = "홍길동")
            @RequestParam(required = false) String ordererName,
            @Parameter(description = "주문자 휴대폰 번호", example = "010-1234-5678")
            @RequestParam(required = false) String ordererPhone,
            @ParameterObject Pageable pageable
    );

    @Operation(
            summary = "주문 상세 조회",
            description = "주문 ID를 통해 해당 주문의 전체 상세 정보(대표 주문 + 개별 상품 목록)를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<OrderHistoryResponse> getOrderDetail(
            @Parameter(description = "주문 ID", example = "10", required = true)
            @PathVariable Long orderId
    );

    @Operation(
            summary = "관리자 전체 주문 조회",
            description = """
                    관리자 권한으로 전체 주문을 페이징 조회합니다.
                    대표 주문 상태(orderStatus)와 개별 상품 상태(unitStatus)로 필터링할 수 있습니다.
                    
                    ※ 이 API는 @RequiredAdmin 권한이 필요합니다.
                    """
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<Page<OrderHistoryResponse>> getAllOrders(
            @Parameter(description = "대표 주문 상태 필터", example = "SHIPPING")
            @RequestParam(required = false) OrderStatusType orderStatus,

            @Parameter(description = "개별 상품 상태 필터", example = "WAITING")
            @RequestParam(required = false) OrderedBookStatusType unitStatus,

            @ParameterObject Pageable pageable
    );
}
