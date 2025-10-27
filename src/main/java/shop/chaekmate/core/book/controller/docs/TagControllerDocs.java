package shop.chaekmate.core.book.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.book.dto.CreateTagRequest;
import shop.chaekmate.core.book.dto.CreateTagResponse;
import shop.chaekmate.core.book.dto.TagResponse;
import shop.chaekmate.core.book.dto.UpdateTagRequest;
import shop.chaekmate.core.book.dto.UpdateTagResponse;

@Tag(name = "태그 관리 API", description = "태그 등록, 수정, 삭제, 조회 관련 API")
public interface TagControllerDocs {

    @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
    @ApiResponse(responseCode = "201", description = "생성 성공")
    CreateTagResponse createTag(@Valid @RequestBody CreateTagRequest createTagRequest);

    @Operation(summary = "태그 단건 조회", description = "ID로 태그를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 태그를 찾을 수 없음")
    TagResponse readTag(@Parameter(description = "조회할 태그 ID", example = "1") @PathVariable(name = "id") Long targetId);

    @Operation(summary = "태그 전체 조회", description = "모든 태그를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    List<TagResponse> readAllTags();

    @Operation(summary = "태그 수정", description = "태그 이름을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 태그를 찾을 수 없음")
    UpdateTagResponse updateTag(@Parameter(description = "수정할 태그 ID", example = "1") @PathVariable(name = "id") Long targetId,
                              @Valid @RequestBody UpdateTagRequest request);

    @Operation(summary = "태그 삭제", description = "태그를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 태그를 찾을 수 없음")
    void deleteTag(@Parameter(description = "삭제할 태그 ID", example = "1") @PathVariable(name = "id") Long targetId);
}
