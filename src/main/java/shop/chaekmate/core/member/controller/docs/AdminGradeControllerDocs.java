package shop.chaekmate.core.member.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;

import java.util.List;

@Tag(name = "Admin Grade", description = "관리자 회원 등급 정책 API")
public interface AdminGradeControllerDocs {

    @Operation(
            summary = "회원 등급 목록 조회",
            description = "모든 회원 등급 정책을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "등급 목록 조회 성공",
                            content = @Content(
                                    array = @ArraySchema(schema = @Schema(implementation = GradeResponse.class))
                            )
                    )
            }
    )
    ResponseEntity<List<GradeResponse>> getAllGrades();


    @Operation(
            summary = "회원 등급 생성",
            description = "새로운 회원 등급 정책을 생성합니다.",
            requestBody = @RequestBody(
                    required = true,
                    description = "등급 생성 요청 값",
                    content = @Content(
                            schema = @Schema(implementation = CreateGradeRequest.class),
                            examples = @ExampleObject(
                                    name = "등급 생성 예시",
                                    value = """
                                            {
                                              "name": "VIP",
                                              "pointRate": 5,
                                              "upgradeStandardAmount": 500000
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "생성 성공")
            }
    )
    ResponseEntity<Void> createGrade(CreateGradeRequest request);


    @Operation(
            summary = "회원 등급 수정",
            description = "특정 회원 등급 정책을 수정합니다.",
            parameters = @Parameter(name = "gradeId", required = true, example = "3"),
            requestBody = @RequestBody(
                    required = true,
                    description = "등급 수정 요청 값",
                    content = @Content(
                            schema = @Schema(implementation = UpdateGradeRequest.class),
                            examples = @ExampleObject(
                                    name = "등급 수정 예시",
                                    value = """
                                            {
                                              "name": "GOLD",
                                              "pointRate": 3,
                                              "upgradeStandardAmount": 300000
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "204", description = "수정 성공"),
                    @ApiResponse(responseCode = "404", description = "등급 없음", content = @Content)
            }
    )
    ResponseEntity<Void> updateGrade(
            @PathVariable Long gradeId,
            UpdateGradeRequest request
    );


    @Operation(
            summary = "회원 등급 삭제",
            description = "특정 회원 등급 정책을 삭제합니다.",
            parameters = {
                    @Parameter(name = "gradeId", required = true, example = "3")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "등급 없음", content = @Content)
            }
    )
    ResponseEntity<Void> deleteGrade(@PathVariable Long gradeId);

}
