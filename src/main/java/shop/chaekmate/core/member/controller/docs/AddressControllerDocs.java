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
import shop.chaekmate.core.member.dto.response.AddressResponse;

import java.util.List;

@Tag(name = "Address", description = "회원 배송지 API")
public interface AddressControllerDocs {

    @Operation(
            summary = "배송지 등록",
            description = "회원에게 새로운 배송지를 등록합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7")
            },
            requestBody = @RequestBody(
                    required = true,
                    description = "배송지 생성 요청 데이터",
                    content = @Content(
                            schema = @Schema(implementation = CreateAddressRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "주소 등록 예시",
                                            value = """
                                                    {
                                                      "streetName": "세종대로 110",
                                                      "detail": "101동 1003호",
                                                      "zipcode": "04524",
                                                      "memo": "문 앞에 놓아주세요"
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "배송지 등록 성공"),
                    @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content),
                    @ApiResponse(responseCode = "409", description = "배송지 최대 개수 초과", content = @Content)
            }
    )
    ResponseEntity<Void> createAddress(
            @PathVariable Long memberId,
            @Valid @org.springframework.web.bind.annotation.RequestBody CreateAddressRequest request
    );

    @Operation(
            summary = "회원의 모든 배송지 조회",
            description = "특정 회원이 등록한 모든 배송지를 반환합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AddressResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "회원 없음", content = @Content)
            }
    )
    ResponseEntity<List<AddressResponse>> getAllAddresses(@PathVariable Long memberId);

    @Operation(
            summary = "특정 배송지 조회",
            description = "회원이 등록한 배송지 중 특정 ID의 배송지를 상세 조회합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7"),
                    @Parameter(name = "addressId", description = "배송지 ID", required = true, example = "3")
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = AddressResponse.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "회원 없음 또는 배송지 없음", content = @Content)
            }
    )
    ResponseEntity<AddressResponse> getAddress(
            @PathVariable Long memberId,
            @PathVariable Long addressId
    );

    @Operation(
            summary = "배송지 삭제",
            description = "회원이 등록한 배송지 중 특정 배송지를 삭제합니다.",
            parameters = {
                    @Parameter(name = "memberId", description = "회원 ID", required = true, example = "7"),
                    @Parameter(name = "addressId", description = "배송지 ID", required = true, example = "3")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "회원 없음 또는 배송지 없음", content = @Content)
            }
    )
    ResponseEntity<Void> deleteAddress(
            @PathVariable Long memberId,
            @PathVariable Long addressId
    );
}
