package shop.chaekmate.core.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.AvailabilityResponse;
import shop.chaekmate.core.member.dto.response.MemberGradeResponse;

@Tag(name = "Member", description = "회원 API")
public interface MemberControllerDocs {

    @Operation(
            summary = "회원 가입",
            description = "새로운 회원을 등록합니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "회원 생성 요청 본문",
                    content = @Content(
                            schema = @Schema(implementation = CreateMemberRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "기본 예시",
                                            value = """
                                                    {
                                                      "loginId": "user123",
                                                      "password": "Pa$$word1234!",
                                                      "name": "홍길동",
                                                      "phone": "010-1234-5678",
                                                      "email": "user@example.com",
                                                      "birthDate": "2000-01-01"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content),
                    @ApiResponse(responseCode = "409", description = "중복(ID/이메일) 충돌", content = @Content)
            }
    )
    ResponseEntity<Void> createMember(
            @Valid
            @org.springframework.web.bind.annotation.RequestBody CreateMemberRequest request
    );

    @Operation(
            summary = "로그인 ID 중복 체크",
            description = "로그인 ID의 사용 가능 여부를 확인합니다.",
            parameters = {
                    @Parameter(
                            name = "loginId",
                            description = "중복 여부를 확인할 로그인 ID",
                            required = true,
                            example = "user123"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "중복 여부 조회 성공",
                            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))
                    )
            }
    )
    ResponseEntity<AvailabilityResponse> checkLoginId(
            @RequestParam("loginId") String loginId
    );

    @Operation(
            summary = "이메일 중복 체크",
            description = "이메일의 사용 가능 여부를 확인합니다.",
            parameters = {
                    @Parameter(
                            name = "email",
                            description = "중복 여부를 확인할 이메일",
                            required = true,
                            example = "user@example.com"
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "중복 여부 조회 성공",
                            content = @Content(schema = @Schema(implementation = AvailabilityResponse.class))
                    )
            }
    )
    ResponseEntity<AvailabilityResponse> checkEmail(
            @RequestParam("email") String email
    );


    @Operation(
            summary = "회원 삭제",
            description = "회원을 삭제합니다. (소프트 삭제인 경우 삭제 표시 처리)",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<Void> deleteMember(@PathVariable Long memberId);
}
