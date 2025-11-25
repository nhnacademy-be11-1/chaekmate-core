package shop.chaekmate.core.order.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.order.dto.request.OrderSaveRequest;
import shop.chaekmate.core.order.dto.response.OrderSaveResponse;

@Tag(name = "주문 API", description = "주문 생성 및 조회 관련 API")
public interface OrderControllerDocs {

    @Operation(
            summary = "주문 생성",
            description = "회원 또는 비회원이 주문서를 제출하여 주문을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "주문 생성 성공",
                            content = @Content(schema = @Schema(implementation = OrderSaveResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 주문 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "401", description = "유효하지 않은 사용자 요청입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<OrderSaveResponse> saveOrder(
            @Parameter(description = "회원 ID (비회원일 경우 null 가능)", example = "1", required = false)
            Long memberId,

            @RequestBody(
                    description = "주문 생성 요청 데이터",
                    required = true,
                    content = @Content(schema = @Schema(implementation = OrderSaveRequest.class))
            )
            OrderSaveRequest request
    );
}
