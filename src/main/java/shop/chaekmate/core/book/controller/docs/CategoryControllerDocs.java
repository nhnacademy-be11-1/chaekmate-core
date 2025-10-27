package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.book.dto.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.CreateCategoryResponse;
import shop.chaekmate.core.book.dto.ReadAllCategoriesResponse;
import shop.chaekmate.core.book.dto.ReadCategoryResponse;
import shop.chaekmate.core.book.dto.UpdateCategoryRequest;
import shop.chaekmate.core.book.dto.UpdateCategoryResponse;

import org.springframework.http.ResponseEntity;

@Tag(name = "카테고리 관리 API", description = "카테고리 등록, 수정, 삭제, 조회 관련 API")
public interface CategoryControllerDocs {

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    ResponseEntity<CreateCategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest);

    @Operation(summary = "카테고리 전체 조회", description = "모든 카테고리를 계층 구조로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<ReadAllCategoriesResponse>> readAllCategories();

    @Operation(summary = "카테고리 단건 조회", description = "ID로 카테고리를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 카테고리를 찾을 수 없음")
    ResponseEntity<ReadCategoryResponse> readCategory(@Parameter(description = "조회할 카테고리 ID", example = "1") @PathVariable(name = "id") Long categoryId);

    @Operation(summary = "카테고리 수정", description = "카테고리 이름 또는 부모 카테고리를 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 카테고리 또는 부모 카테고리를 찾을 수 없음")
    ResponseEntity<UpdateCategoryResponse> updateCategory(@Parameter(description = "수정할 카테고리 ID", example = "1") @PathVariable(name = "id") Long categoryId,
                                        @Valid @RequestBody UpdateCategoryRequest updateCategoryRequest);

    @Operation(summary = "카테고리 삭제", description = "카테고리를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 카테고리를 찾을 수 없음")
    ResponseEntity<Void> deleteCategory(@Parameter(description = "삭제할 카테고리 ID", example = "1") @PathVariable(name = "id") Long categoryId);
}
