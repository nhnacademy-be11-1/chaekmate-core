package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "도로명주소 등록 요청 DTO")
public record CreateAddressRequest(

        @Schema(description = "메모(선택)", example = "회사 배송지", required = false)
        String memo,

        @NotBlank(message = "도로명주소를 입력해야 합니다.")
        @Pattern(
                regexp = "^(?=.*(?:대로|로|길))([가-힣0-9\\s]+(?:대로|로|길)\\s\\d{1,4}(?:-\\d{1,3})?(?:[ ,].*)?)$",
                message = "도로명주소 형식이 아닙니다. 예: 서울특별시 중구 세종대로 110"
        )
        @Schema(
                description = "도로명주소 (지번 불가). 예: '서울특별시 중구 세종대로 110'",
                example = "서울특별시 중구 세종대로 110",
                required = true
        )
        String streetName,

        @NotBlank(message = "상세 주소를 입력하세요.")
        @Schema(description = "상세주소 (동/층/호 등)", example = "1층 우측", required = true)
        String detail,

        @Positive(message = "유효한 우편번호를 입력하세요.")
        @Schema(description = "우편번호(숫자)", example = "34940", required = true)
        int zipcode
) {}
