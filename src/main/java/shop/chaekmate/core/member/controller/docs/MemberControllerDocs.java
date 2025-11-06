package shop.chaekmate.core.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberResponse;

@Tag(name = "회원 관리 API", description = "회원 생성, 조회, 수정, 삭제 관련 API 문서")
public interface MemberControllerDocs {

    @Operation(
            summary = "회원 생성",
            description = "새로운 회원을 등록합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원 생성 성공")
    ResponseEntity<MemberResponse> createMember(
            @Valid @RequestBody CreateMemberRequest request);

    @Operation(
            summary = "회원 단건 조회",
            description = "회원 ID를 이용해 특정 회원 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 회원을 찾을 수 없음")
    ResponseEntity<MemberResponse> readMember(
            @Parameter(description = "조회할 회원 ID", example = "1")
            @PathVariable Long id);

    @Operation(
            summary = "회원 전체 조회",
            description = "모든 회원 정보를 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<MemberResponse>> readAllMembers();

    @Operation(
            summary = "회원 수정",
            description = "회원의 이름, 이메일, 연락처 등 정보를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 성공")
    @ApiResponse(responseCode = "404", description = "해당 ID의 회원을 찾을 수 없음")
    ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "수정할 회원 ID", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateMemberRequest request);

    @Operation(
            summary = "회원 삭제",
            description = "해당 ID의 회원을 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공 (내용 없음)")
    @ApiResponse(responseCode = "404", description = "해당 ID의 회원을 찾을 수 없음")
    ResponseEntity<Void> deleteMember(
            @Parameter(description = "삭제할 회원 ID", example = "1")
            @PathVariable Long id);
}
