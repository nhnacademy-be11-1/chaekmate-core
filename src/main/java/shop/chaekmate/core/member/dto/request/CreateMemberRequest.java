package shop.chaekmate.core.member.dto.request;


import jakarta.validation.constraints.*;
import shop.chaekmate.core.member.entity.type.PlatformType;

import java.time.LocalDate;

public record CreateMemberRequest(
        @NotBlank(message = "ID는 필수입니다.") @Size(max = 20, message = "ID는 20자 이하로 입력해주세요.") String loginId,
        @NotBlank(message = "비밀번호는 필수입니다.") @Size(min = 8, max = 100, message = "비밀번호는 8자 이상, 100자 이하로 입력해주세요.") String password,  // 해시 전 원문 길이 체크
        @NotBlank(message = "이름은 필수입니다.") @Size(max = 50, message = "이름은 50자 이하로 입력해주세요.") String name,
        @NotBlank(message = "전화번호는 필수입니다.") @Pattern(regexp = "^[0-9\\-]{9,20}$", message = "전화번호 형식이 올바르지 않습니다.")
        String phone,
        @NotBlank(message = "이메일은 필수입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.") String email,
        @NotNull(message = "생년월일은 필수입니다.")LocalDate birthDate
) {}