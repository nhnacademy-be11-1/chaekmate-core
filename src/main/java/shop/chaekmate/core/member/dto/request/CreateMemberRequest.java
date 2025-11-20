package shop.chaekmate.core.member.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import shop.chaekmate.core.member.entity.type.PlatformType;

import java.time.LocalDate;

@Schema(description = "회원 생성 요청 DTO")
public record CreateMemberRequest(

        @NotBlank(message = "ID는 필수입니다.")
        @Size(max = 100, message = "ID는 100자 이하로 입력해주세요.")
        @Schema(description = "로그인용 ID (PAYCO의 경우 idNo 사용)", example = "5c8e0390-7775-11e8-ac6f-005056ac5e22", maxLength = 100, required = true)
        String loginId,

        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상, 100자 이하로 입력해주세요.")
        @Schema(description = "비밀번호 (PAYCO 회원가입 시 값은 무시되고 랜덤 생성)", example = "Pa$$word1234!", minLength = 8, required = false)
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
        @Schema(description = "사용자 이름", example = "홍길동", maxLength = 50, required = true)
        String name,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^[0-9\\-]{9,20}$", message = "전화번호 형식이 올바르지 않습니다.")
        @Schema(description = "전화번호(숫자, 하이픈 포함 가능)", example = "010-1234-5678", required = true)
        String phone,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
        @Schema(description = "이메일 주소", example = "user@example.com", required = true)
        String email,

        @NotNull(message = "생년월일은 필수입니다.")
        @Schema(description = "생년월일(YYYY-MM-DD 형식)", example = "2000-01-01", required = true)
        LocalDate birthDate

) {}