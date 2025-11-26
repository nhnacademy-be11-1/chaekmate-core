package shop.chaekmate.core.review.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import shop.chaekmate.core.book.dto.response.PageResponse;
import shop.chaekmate.core.common.dto.ErrorResponse;
import shop.chaekmate.core.review.dto.request.CreateReviewRequest;
import shop.chaekmate.core.review.dto.request.UpdateReviewRequest;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;
import shop.chaekmate.core.review.dto.response.ReadReviewResponse;
import shop.chaekmate.core.review.dto.response.UpdateReviewResponse;

@Tag(name = "리뷰 관리 API", description = "리뷰 등록, 조회, 수정, 삭제 관련 API")
public interface ReviewControllerDocs {

    @Operation(
            summary = "리뷰 생성",
            description = "새로운 리뷰를 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "리뷰 생성 성공",
                            content = @Content(schema = @Schema(implementation = CreateReviewResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 또는 주문 도서를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<CreateReviewResponse> createReview(
            @RequestBody(
                    description = "생성할 리뷰 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateReviewRequest.class))
            )
            CreateReviewRequest request
    );

    @Operation(
            summary = "리뷰 단건 조회",
            description = "ID로 리뷰를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = ReadReviewResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<ReadReviewResponse> readReview(
            @Parameter(description = "조회할 리뷰 ID", example = "1")
            @PathVariable(name = "id") Long reviewId
    );

    @Operation(
            summary = "도서별 리뷰 페이지네이션 조회",
            description = "특정 도서의 리뷰를 페이지네이션으로 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 도서를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<PageResponse<ReadReviewResponse>> readReviewsByBookId(
            @Parameter(description = "조회할 도서 ID", example = "1")
            @PathVariable("bookId") Long bookId,
            @Parameter(description = "페이징 정보 (page: 페이지 번호, size: 페이지 크기)", example = "page=0&size=10")
            Pageable pageable
    );

    @Operation(
            summary = "리뷰 수정",
            description = "리뷰의 내용과 별점을 수정합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(schema = @Schema(implementation = UpdateReviewResponse.class))),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<UpdateReviewResponse> updateReview(
            @Parameter(description = "수정할 리뷰 ID", example = "1")
            @PathVariable(name = "id") Long reviewId,
            @RequestBody(
                    description = "수정할 리뷰 정보",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UpdateReviewRequest.class))
            )
            UpdateReviewRequest request
    );

    @Operation(
            summary = "리뷰 삭제",
            description = "리뷰를 삭제합니다. (Soft Delete)",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "해당 ID의 리뷰를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
            }
    )
    ResponseEntity<Void> deleteReview(
            @Parameter(description = "삭제할 리뷰 ID", example = "1")
            @PathVariable(name = "id") Long reviewId
    );
}

