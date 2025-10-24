package shop.chaekmate.core.order.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.dto.response.WrapperResponse;

@Tag(name = "포장지 관리 API", description = "포장지 등록 수정 삭제 조회 관련 API")
@RequestMapping("/wrappers")
public interface WrapperApi {
    @Operation(summary = "포장지 등록", description = "새로운 포장지를 등록합니다.")
    @ApiResponse(responseCode = "201", description = "등록 성공")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 이름")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/admin")
    WrapperResponse createWrapper(@Valid @RequestBody WrapperRequest request);

    @Operation(summary = "포장지 수정", description = "포장지 이름 또는 가격을 수정합니다.")
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 포장지를 찾을 수 없음")
    @ApiResponse(responseCode = "409", description = "이미 존재하는 이름")
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/admin/{id}")
    WrapperResponse modifyWrapper(
            @Parameter(description = "수정할 포장지 ID", example = "1")
            @PathVariable(name = "id") Long wrapperId,
            @Valid @RequestBody WrapperRequest request
    );

    @Operation(summary = "포장지 삭제", description = "포장지를 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @ApiResponse(responseCode = "404", description = "해당 포장지를 찾을 수 없음")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/admin/{id}")
    void deleteWrapper(
            @Parameter(description = "삭제할 포장지 ID", example = "1")
            @PathVariable(name = "id") Long id
    );

    @Operation(summary = "포장지 단건 조회", description = "ID로 포장지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 포장지를 찾을 수 없음")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    WrapperResponse getWrapperById(
            @Parameter(description = "조회할 포장지 ID", example = "1") @PathVariable Long id
    );

    @Operation(summary = "포장지 전체 조회", description = "모든 포장지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    List<WrapperResponse> getWrappers();
}