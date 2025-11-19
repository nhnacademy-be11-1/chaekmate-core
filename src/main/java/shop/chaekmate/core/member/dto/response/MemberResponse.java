package shop.chaekmate.core.member.dto.response;

import java.time.LocalDate;

public record MemberResponse(
        String loginId,
        String name,
        String phone,
        String email,
        LocalDate birthDate
) {}