package shop.chaekmate.core.order.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.dto.response.WrapperResponse;

@Tag(name = "포장지 관리 API", description = "포장지 등록 수정 삭제 조회 관련 API")
public interface WrapperControllerDocs {

    @Operation(
            summary = "포장지 등록",
            description = "새로운 포장지를 등록합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "포장지 등록 성공",
                            content = @Content(schema = @Schema(implementation = WrapperResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 포장지입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<WrapperResponse> createWrapper(
            @RequestBody(
                    description = "등록할 포장지 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WrapperRequest.class)
                    )
            )
            WrapperRequest request
    );

    @Operation(
            summary = "포장지 수정",
            description = "포장지 이름 또는 가격을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "포장지 수정 성공",
                            content = @Content(schema = @Schema(implementation = WrapperResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 포장지를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 포장지입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<WrapperResponse> modifyWrapper(
            @Parameter(description = "수정할 포장지 ID", example = "1")
            Long wrapperId,
            @RequestBody(
                    description = "수정할 포장지 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = WrapperRequest.class))
            )
            WrapperRequest request
    );

    @Operation(
            summary = "포장지 삭제",
            description = "포장지를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "포장지 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 포장지를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            }
    )
    ResponseEntity<Void> deleteWrapper(
            @Parameter(description = "삭제할 포장지 ID", example = "1")
            Long id
    );

    @Operation(
            summary = "포장지 단건 조회",
            description = "ID로 포장지를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "해당 포장지 조회 성공",
                            content = @Content(schema = @Schema(implementation = WrapperResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 포장지를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),

            }
    )
    ResponseEntity<WrapperResponse> getWrapperById(
            @Parameter(description = "조회할 포장지 ID", example = "1")
            Long id
    );

    @Operation(
            summary = "포장지 전체 조회",
            description = "모든 포장지를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "전체 포장지 조회 성공",
                            content = @Content(schema = @Schema(implementation = WrapperResponse.class)))
            }
    )
    ResponseEntity<List<WrapperResponse>> getWrappers();
}