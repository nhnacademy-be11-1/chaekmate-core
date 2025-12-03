package shop.chaekmate.core.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import shop.chaekmate.core.member.dto.response.MemberResponse;

import java.util.List;

@Tag(name = "Admin Member", description = "관리자 회원 관리 API")
public interface AdminMemberControllerDocs {

    @Operation(
            summary = "회원 목록 조회 (활성/탈퇴)",
            description = """
                    관리자 페이지에서 회원 목록을 조회합니다.
                    status 파라미터를 통해 활성(ACTIVE) 또는 탈퇴(DELETED) 회원만 조회할 수 있습니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "status",
                            description = "조회할 회원 상태 (ACTIVE / DELETED)",
                            required = false,
                            example = "ACTIVE"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 목록 조회 성공",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = MemberResponse.class)))
                    )
            }
    )
    ResponseEntity<List<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "ACTIVE") String status
    );


    @Operation(
            summary = "회원 삭제",
            description = """
                    특정 회원을 삭제합니다. (Soft Delete 방식)
                    deleted_at 이 설정되어 '탈퇴 회원' 상태가 됩니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "memberId",
                            description = "삭제할 회원 ID",
                            required = true,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<Void> deleteMemberById(@PathVariable Long memberId);


    @Operation(
            summary = "탈퇴 회원 복구",
            description = """
                    탈퇴된 회원을 복구합니다.
                    deleted_at 값을 null 로 변경하여 다시 활성 회원 상태로 전환됩니다.
                    """,
            parameters = {
                    @Parameter(
                            name = "memberId",
                            description = "복구할 회원 ID",
                            required = true,
                            example = "10"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "복구 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<Void> restoreMemberById(@PathVariable Long memberId);
}
