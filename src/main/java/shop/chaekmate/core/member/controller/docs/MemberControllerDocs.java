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
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
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
    ResponseEntity<Void> createMember(@Valid @org.springframework.web.bind.annotation.RequestBody CreateMemberRequest request);

    @Operation(
            summary = "회원 등급 조회",
            description = "회원의 등급을 조회합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = MemberGradeResponse.class))),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<MemberGradeResponse> getMemberGrade(@PathVariable Long memberId);

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

    @Operation(
            summary = "회원 주소 등록",
            description = "회원 주소를 등록합니다. 도로명주소만 허용되며, 지번은 거부됩니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "주소 생성 요청 본문(도로명만 허용)",
                    content = @Content(
                            schema = @Schema(implementation = CreateAddressRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "도로명주소 예시",
                                            value = """
                        {
                          "memo": "회사 배송지",
                          "streetName": "서울특별시 중구 세종대로 110",
                          "detail": "1층 우측",
                          "zipcode": 34940
                        }
                        """
                                    )
                            }
                    )
            ),
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7")
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "주소 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패(정규식/NotBlank 등)", content = @Content),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<Void> createMemberAddress(@PathVariable Long memberId,
                                             @Valid @org.springframework.web.bind.annotation.RequestBody CreateAddressRequest request);
}
