package shop.chaekmate.core.payment.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.base.PaymentResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;

@Tag(name = "결제 API", description = "결제 승인 및 취소 관련 API")
public interface PaymentControllerDocs {

    @Operation(
            summary = "결제 승인",
            description = "PG사(Toss, Payco, NaverPay 등)로부터 받은 정보를 기반으로 결제를 승인합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 승인 성공",
                            content = @Content(schema = @Schema(implementation = PaymentApproveResponse.class))),
                    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않거나 금액 불일치 등의 오류가 발생했습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 또는 외부 결제 모듈 오류입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<PaymentResponse> approve(
            @Parameter(
                    name = "X-Member-Id",
                    description = "회원 ID",
                    required = false
            )
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody(
                    description = "결제 승인 요청 정보 (orderNumber, paymentKey, amount 등)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentApproveRequest.class))
            )
            PaymentApproveRequest request
    );

    @Operation(
            summary = "결제 취소",
            description = "승인된 결제를 취소합니다. 부분 취소도 가능합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 취소 성공",
                            content = @Content(schema = @Schema(implementation = PaymentCancelResponse.class))),
                    @ApiResponse(responseCode = "400", description = "요청 데이터가 유효하지 않거나 취소 불가능한 상태입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "취소하려는 결제 정보를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류 또는 외부 결제 모듈 오류입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<PaymentCancelResponse> cancel(
            @Parameter(
                    name = "X-Member-Id",
                    description = "회원 ID, 비회원 NULL",
                    required = false
            )
            @RequestHeader("X-Member-Id") Long memberId,
            @RequestBody(
                    description = "결제 취소 요청 정보 (paymentKey, cancelAmount, cancelReason 등)",
                    required = true,
                    content = @Content(schema = @Schema(implementation = PaymentCancelRequest.class))
            )
            PaymentCancelRequest request
    );
}
