package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.book.dto.request.BookSearchCondition;
import shop.chaekmate.core.book.dto.request.RankingType;
import shop.chaekmate.core.book.dto.response.BookQuerySliceResponse;

@Tag(name = "도서 조회 API", description = "메인 페이지 및 도서 목록 관련 조회 API")
public interface BookQueryControllerDocs {

    @Operation(summary = "최근 추가된 도서 조회", description = "최근에 추가된 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getRecentlyAddedBooks(
            @ParameterObject Pageable pageable);

    @Operation(summary = "맞춤 추천 도서 조회", description = "사용자 맞춤 추천 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getPersonalizedRecommendedBooks(
            @ParameterObject Pageable pageable);

    @Operation(summary = "리뷰 많은 도서 조회 (30일)", description = "최근 30일간 리뷰가 많이 달린 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getTopReviewedBooksForLast30Days(
            @ParameterObject Pageable pageable);

    @Operation(summary = "얼리어답터의 선택 도서 조회", description = "최근 30일 이내에 가장 많은 회원이 주문한 책 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getEarlyAdopterPicks(
            @ParameterObject Pageable pageable);

    @Operation(summary = "책메이트 추천 도서 조회", description = "책메이트가 추천하는 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getChaekmateRecommendedBooks(
            @ParameterObject Pageable pageable);

    @Operation(summary = "신간 도서 조회", description = "새로 출간된 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getNewBooks(
            @ParameterObject Pageable pageable);

    @Operation(summary = "전체 도서 조회", description = "검색 조건과 함께 전체 도서 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getAllBooks(
            @ParameterObject BookSearchCondition condition,
            @ParameterObject Pageable pageable);

    @Operation(summary = "도서 랭킹 조회", description = "타입(VIEWS, SALES)에 따라 도서 랭킹 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getBookRanking(
            @Parameter(description = "랭킹 타입 (VIEWS 또는 SALES)", required = true) RankingType type,
            @ParameterObject Pageable pageable);

    @Operation(summary = "베스트셀러 조회", description = "베스트셀러 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<BookQuerySliceResponse> getBestsellers(
            @ParameterObject Pageable pageable);
}
