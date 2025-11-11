package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.book.dto.request.BookImageAddRequest;
import shop.chaekmate.core.book.dto.request.ThumbnailUpdateRequest;
import shop.chaekmate.core.book.dto.response.BookImageResponse;

import java.util.List;

@Tag(name = "도서 이미지 API", description = "도서 이미지 관리 API")
public interface BookImageControllerDocs {

    @Operation(summary = "도서 이미지 추가", description = "특정 도서에 새로운 이미지를 추가합니다. 가장 처음 추가하는 이미지가 썸네일이 됩니다.")
    ResponseEntity<BookImageResponse> addImage(
            @Parameter(description = "도서 ID") @PathVariable Long bookId,
            @Valid @RequestBody BookImageAddRequest request);

    @Operation(summary = "도서 썸네일 조회", description = "가장 먼저 추가된 이미지를 썸네일로 조회합니다.")
    ResponseEntity<BookImageResponse> getThumbnail(
            @Parameter(description = "도서 ID") @PathVariable Long bookId);

    @Operation(summary = "도서 상세 이미지 목록 조회", description = "썸네일을 제외한 모든 이미지를 조회합니다.")
    ResponseEntity<List<BookImageResponse>> getDetailImages(
            @Parameter(description = "도서 ID") @PathVariable Long bookId);

    @Operation(summary = "도서의 모든 이미지 조회", description = "썸네일(첫 번째 이미지)과 상세 이미지를 모두 조회합니다.")
    ResponseEntity<List<BookImageResponse>> getAllImages(
            @Parameter(description = "도서 ID") @PathVariable Long bookId);

    @Operation(summary = "도서 썸네일 수정", description = "특정 도서의 썸네일 이미지 URL을 수정합니다.")
    ResponseEntity<Void> updateThumbnail(
            @Parameter(description = "도서 ID") @PathVariable Long bookId,
            @Valid @RequestBody ThumbnailUpdateRequest request);

    @Operation(summary = "이미지 삭제", description = "특정 도서에 속한 이미지를 삭제합니다.")
    ResponseEntity<Void> deleteImage(
            @Parameter(description = "도서 ID") @PathVariable Long bookId,
            @Parameter(description = "이미지 ID") @PathVariable Long imageId);
}
