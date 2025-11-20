package shop.chaekmate.core.member.dto.response;

import shop.chaekmate.core.member.entity.Member;
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
) {
    public static MemberResponse from(Member entity){
        return new MemberResponse(
                entity.getId(),
                entity.getLoginId(),
                entity.getName(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getPlatformType(),
                entity.getLastLoginAt()
        );
    }
}