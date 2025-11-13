package shop.chaekmate.core.member.dto.response;


import shop.chaekmate.core.member.entity.type.PlatformType;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String loginId,
        String name,
        String phone,
        String email,
        LocalDate birthDate,
        PlatformType platformType,
        LocalDateTime lastLoginAt
) {}