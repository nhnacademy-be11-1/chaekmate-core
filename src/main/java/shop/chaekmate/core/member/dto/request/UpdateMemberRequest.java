package shop.chaekmate.core.member.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;

public record UpdateMemberRequest(
        @NotBlank(message = "이름은 필수입니다.") @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.") String name,
        @NotBlank(message = "전화번호는 필수입니다.") @Pattern(regexp = "^[0-9\\-]{9,20}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone,
        @NotBlank(message = "이메일은 필수입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.") String email
) {}
