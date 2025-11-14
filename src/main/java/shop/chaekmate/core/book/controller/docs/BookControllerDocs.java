package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookCreateResponse;
import shop.chaekmate.core.book.dto.response.BookListResponse;
import shop.chaekmate.core.book.dto.response.BookResponse;
import shop.chaekmate.core.book.dto.response.BookSummaryResponse;

import java.util.List;

@Tag(name = "도서 관리 API", description = "도서 등록, 수정, 삭제, 조회 관련 API")
public interface BookControllerDocs {

    @Operation(
            summary = "도서 생성",
            description = "새로운 도서를 생성합니다."
    )
    @ApiResponse(responseCode = "201", description = "생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    ResponseEntity<BookCreateResponse> createBook(
            @Valid @RequestBody BookCreateRequest request
    );

    @Operation(
            summary = "도서 수정",
            description = "도서 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 도서를 찾을 수 없음")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    ResponseEntity<Void> updateBook(
            @Parameter(description = "수정할 도서 ID", example = "1", required = true)
            @PathVariable Long bookId,
            @Valid @RequestBody BookUpdateRequest request
    );

    @Operation(
            summary = "도서 삭제",
            description = "도서를 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 도서를 찾을 수 없음")
    ResponseEntity<Void> deleteBook(
            @Parameter(description = "삭제할 도서 ID", example = "1", required = true)
            @PathVariable Long bookId
    );

    @Operation(
            summary = "도서 단건 조회",
            description = "ID로 도서 상세 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 도서를 찾을 수 없음")
    ResponseEntity<BookResponse> getBook(
            @Parameter(description = "조회할 도서 ID", example = "1", required = true)
            @PathVariable Long bookId
    );

    @Operation(
            summary = "도서 대량 조회",
            description = "여러 도서 ID 목록으로 도서 요약 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<BookSummaryResponse>> getBooksByIds(
            @Parameter(
                    description = "조회할 도서 ID 목록",
                    example = "1,2,3",
                    required = true
            )
            @RequestParam List<Long> bookIds
    );

    @Operation(
            summary = "도서 목록 조회",
            description = "카테고리, 태그, 키워드 중 **정확히 1개의 조건**으로 도서 목록을 조회합니다. " +
                    "페이징을 지원합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "400", description = "검색 조건이 없거나 2개 이상인 경우")
    ResponseEntity<Page<BookListResponse>> getBookList(
            @Parameter(
                    description = "카테고리 ID로 검색 (categoryId, tagId, keyword 중 1개만 사용 가능)",
                    example = "1"
            )
            @RequestParam(required = false) Long categoryId,

            @Parameter(
                    description = "태그 ID로 검색 (categoryId, tagId, keyword 중 1개만 사용 가능)",
                    example = "5"
            )
            @RequestParam(required = false) Long tagId,

            @Parameter(
                    description = "키워드로 검색 (제목/저자/출판사) (categoryId, tagId, keyword 중 1개만 사용 가능)",
                    example = "자바"
            )
            @RequestParam(required = false) String keyword,

            @Parameter(
                    description = "페이징 정보 (page: 페이지 번호, size: 페이지 크기)",
                    example = "page=0&size=10"
            )
            @PageableDefault(size = 10, page = 0) Pageable pageable
    );
}
