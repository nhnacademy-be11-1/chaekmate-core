package shop.chaekmate.core.order.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyRequest;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyHistoryResponse;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyResponse;


@Tag(name = "배송 정책 API", description = "배송비 정책 관리 및 조회 관련 API")
public interface DeliveryControllerDocs {

    @Operation(
            summary = "배송 정책 등록",
            description = "새로운 배송 정책을 등록합니다. 기존 정책이 존재하면 자동으로 삭제 후 새 정책을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "배송 정책 등록 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryPolicyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<DeliveryPolicyResponse> createDeliveryPolicy(
            @RequestBody(
                    description = "등록할 배송 정책",
                    required = true,
                    content = @Content(schema = @Schema(implementation = DeliveryPolicyRequest.class))
            )
            DeliveryPolicyRequest request
    );

    @Operation(summary = "배송 정책 이력 전체 조회",
            description = "관리자가 기존 배송 정책 이력을 최신순으로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "배송 정책 이력 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeliveryPolicyHistoryResponse.class)))),
                    @ApiResponse(responseCode = "404", description = "활성화된 배송 정책이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    ResponseEntity<Page<DeliveryPolicyHistoryResponse>> getDeliveryPolicies(
            @ParameterObject @Parameter(description = "정책 내역 정보", example = "page=0&size=15") Pageable pageable
    );

    @Operation(summary = "현재 배송 정책 조회",
            description = "현재 활성화된 배송 정책을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현재 배송 정책 조회 성공",
                            content = @Content(schema = @Schema(implementation = DeliveryPolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "활성화된 배송 정책이 존재하지 않습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    ResponseEntity<DeliveryPolicyResponse> getCurrentDeliveryPolicy();
}
