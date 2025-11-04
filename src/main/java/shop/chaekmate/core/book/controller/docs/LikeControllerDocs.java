package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import shop.chaekmate.core.book.dto.response.LikeResponse;

import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "좋아요 관리 API", description = "좋아요 생성, 삭제, 조회 관련 API")
public interface LikeControllerDocs {

    @Operation(summary = "좋아요 생성", description = "책에 좋아요를 추가합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "404", description = "해당 책 또는 회원을 찾을 수 없음")
    ResponseEntity<LikeResponse> createLike(
            @Parameter(description = "좋아요를 추가할 책 ID", example = "1") @PathVariable Long bookId,
            HttpServletRequest request);

    @Operation(summary = "좋아요 단건 조회", description = "ID로 좋아요를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 좋아요를 찾을 수 없음")
    ResponseEntity<LikeResponse> readLike(
            @Parameter(description = "조회할 좋아요 ID", example = "1") @PathVariable(name = "likeId") Long likeId);

    @Operation(summary = "책별 좋아요 목록 조회", description = "책 ID로 해당 책의 모든 좋아요를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<LikeResponse>> getBookLikes(
            @Parameter(description = "조회할 책 ID", example = "1") @PathVariable Long bookId);

    @Operation(summary = "회원별 좋아요 목록 조회", description = "JWT 토큰에서 회원 ID를 추출하여 해당 회원의 모든 좋아요를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<LikeResponse>> getMemberLikes(HttpServletRequest request);

    @Operation(summary = "좋아요 ID로 삭제", description = "좋아요 ID로 좋아요를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 좋아요를 찾을 수 없음")
    ResponseEntity<Void> deleteLikeById(
            @Parameter(description = "삭제할 좋아요 ID", example = "1") @PathVariable Long likeId);

    @Operation(summary = "책과 회원 ID로 삭제", description = "JWT 토큰에서 회원 ID를 추출하여 책 ID와 함께 좋아요를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 좋아요를 찾을 수 없음")
    ResponseEntity<Void> deleteLikeByBookIdAndMemberId(
            @Parameter(description = "좋아요를 삭제할 책 ID", example = "1") @PathVariable Long bookId,
            HttpServletRequest request);
}
