package shop.chaekmate.core.point.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointResponse;

@Tag(name = "포인트 히스토리 관리 API", description = "포인트 히스토리 조회 및 적립/사용 관리 API")
public interface PointHistoryControllerDocs {

    @Operation(
            summary = "회원 포인트 히스토리 전체 조회",
            description = "특정 회원의 모든 포인트 히스토리를 페이징하여 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Page<PointHistoryResponse>> getPointHistory(
            @Parameter(description = "회원 ID", required = true, example = "1")
            Long memberId,
            @Parameter(description = "페이지 정보", example = "page=0&size=10")
            Pageable pageable
    );

    @Operation(
            summary = "회원 보유 포인트 조회",
            description = "회원의 현재 보유 포인트를 조회합니다. (적립 포인트 - 사용 포인트)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = PointResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<PointResponse> getMemberPoint(
            @Parameter(description = "회원 ID", required = true, example = "1")
            Long memberId
    );

    @Operation(
            summary = "포인트 적립",
            description = "회원에게 포인트를 적립합니다. (회원가입, 주문 완료, 리뷰 작성 등)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "적립 성공",
                            content = @Content(schema = @Schema(implementation = CreatePointHistoryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<CreatePointHistoryResponse> earnPoint(
            @Parameter(description = "회원 ID", required = true, example = "1")
            Long memberId,
            @RequestBody(description = "적립할 포인트 정보", required = true,
                    content = @Content(schema = @Schema(implementation = CreatePointHistoryRequest.class)))
            CreatePointHistoryRequest request
    );

    @Operation(
            summary = "포인트 사용",
            description = "회원의 포인트를 사용합니다. (주문 결제 등)",
            responses = {
                    @ApiResponse(responseCode = "201", description = "사용 성공",
                            content = @Content(schema = @Schema(implementation = CreatePointHistoryResponse.class))),
                    @ApiResponse(responseCode = "400", description = "포인트 부족 또는 잘못된 요청 데이터",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<CreatePointHistoryResponse> spendPoint(
            @Parameter(description = "회원 ID", required = true, example = "1")
            Long memberId,
            @RequestBody(description = "사용할 포인트 정보", required = true,
                    content = @Content(schema = @Schema(implementation = CreatePointHistoryRequest.class)))
            CreatePointHistoryRequest request
    );
}
