package shop.chaekmate.core.point.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.response.CreatePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.ReadPointPolicyResponse;
import shop.chaekmate.core.point.dto.response.UpdatePointPolicyResponse;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.common.dto.ErrorResponse;

@Tag(name = "포인트 관리 API", description = "포인트 정책 조회 및 관리 API")
public interface PointPolicyCotrollerDocs {

    @Operation(
            summary = "포인트 정책 조회",
            description = "주어진 타입의 포인트 정책을 조회합니다. (관리자/회원용 동일 응답)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ReadPointPolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<ReadPointPolicyResponse> getPolicy(
            @Parameter(description = "포인트 적립 타입", required = true, schema = @Schema(implementation = PointEarnedType.class))
            PointEarnedType type
    );

    @Operation(
            summary = "포인트 정책 등록",
            description = "새로운 포인트 정책을 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "등록 성공",
                            content = @Content(schema = @Schema(implementation = CreatePointPolicyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<CreatePointPolicyResponse> createPointPolicy(
            @RequestBody(description = "등록할 포인트 정책 정보", required = true,
                    content = @Content(schema = @Schema(implementation = CreatePointPolicyRequest.class)))
            CreatePointPolicyRequest request
    );

    @Operation(
            summary = "포인트 정책 수정",
            description = "지정한 타입의 포인트 정책을 업데이트합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = UpdatePointPolicyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<UpdatePointPolicyResponse> updatePointPolicy(
            @Parameter(description = "수정할 포인트 타입", required = true, schema = @Schema(implementation = PointEarnedType.class))
            PointEarnedType type,

            @RequestBody(description = "수정할 내용", required = true,
                    content = @Content(schema = @Schema(implementation = UpdatePointPolicyRequest.class)))
            UpdatePointPolicyRequest request
    );

    @Operation(
            summary = "포인트 정책 삭제",
            description = "지정한 타입의 포인트 정책을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공", content = @Content),
                    @ApiResponse(responseCode = "404", description = "정책을 찾을 수 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> delete(
            @Parameter(description = "삭제할 포인트 타입", required = true, schema = @Schema(implementation = PointEarnedType.class))
            PointEarnedType type
    );
}
