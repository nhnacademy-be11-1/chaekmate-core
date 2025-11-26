package shop.chaekmate.core.review.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.review.dto.request.ReviewImageAddRequest;
import shop.chaekmate.core.review.dto.response.ReviewImageResponse;

import java.util.List;

@Tag(name = "Review Image", description = "리뷰 이미지 API")
public interface ReviewImageControllerDocs {

    @Operation(summary = "리뷰 이미지 추가", description = "리뷰에 이미지를 추가합니다.")
    ResponseEntity<List<ReviewImageResponse>> addImages(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewImageAddRequest request);

    @Operation(summary = "리뷰 이미지 조회", description = "리뷰의 모든 이미지를 조회합니다.")
    ResponseEntity<List<ReviewImageResponse>> getImages(
            @PathVariable Long reviewId);

    @Operation(summary = "리뷰 이미지 삭제", description = "리뷰의 이미지를 삭제합니다.")
    ResponseEntity<Void> deleteImage(
            @PathVariable Long reviewId,
            @PathVariable Long imageId);
}
