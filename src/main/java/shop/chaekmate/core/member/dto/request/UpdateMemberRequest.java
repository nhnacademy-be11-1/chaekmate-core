package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "회원 수정 요청")
public record UpdateMemberRequest(
        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.")
        @Schema(description = "사용자 이름", example = "홍길동", maxLength = 50)
        String name,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(regexp = "^[0-9\\-]{9,20}$", message = "전화번호 형식이 올바르지 않습니다.")
        @Schema(description = "전화번호(숫자, 하이픈 포함 가능)", example = "010-1234-5678")
        String phone,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.")
        @Schema(description = "이메일 주소", example = "user@example.com")
        String email
) {
}
