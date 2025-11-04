package shop.chaekmate.core.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import java.util.List;

public interface MemberControllerDocs {

    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다. platformType은 서버가 LOCAL로 설정됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "400", description = "검증 실패"),
            @ApiResponse(responseCode = "409", description = "loginId 또는 email 중복")
    })
    ResponseEntity<MemberResponse> createMember(@Valid CreateMemberRequest request);

    @Operation(summary = "회원 단건 조회", description = "ID로 회원 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<MemberResponse> readMember(
            @Parameter(description = "회원 ID", example = "1") Long id);

    @Operation(summary = "회원 전체 조회", description = "모든 회원을 조회합니다. (관리자용)")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    ResponseEntity<List<MemberResponse>> readAllMembers();

    @Operation(summary = "회원 정보 수정", description = "이름/연락처/이메일을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(schema = @Schema(implementation = MemberResponse.class))),
            @ApiResponse(responseCode = "404", description = "회원 없음"),
            @ApiResponse(responseCode = "409", description = "이메일 중복")
    })
    ResponseEntity<MemberResponse> updateMember(
            @Parameter(description = "회원 ID", example = "1") Long id,
            @Valid UpdateMemberRequest request);

    @Operation(summary = "회원 삭제", description = "소프트 삭제(탈퇴) 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "회원 없음")
    })
    ResponseEntity<Void> deleteMember(
            @Parameter(description = "회원 ID", example = "1") Long id);
}
