package shop.chaekmate.core.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "비밀번호 검증")
public record VerifyPasswordRequest(
        @NotBlank
        @Size(min = 8, max = 100, message = "비밀번호는 8자 이상, 100자 이하로 입력해주세요.")
        @Schema(description = "비밀번호 (LOCAL인 경우에만 인증)", example = "Pa$$word1234!", minLength = 8)
        String password
) {
}
