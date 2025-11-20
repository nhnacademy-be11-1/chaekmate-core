package shop.chaekmate.core.member.dto.response;

import shop.chaekmate.core.member.entity.Grade;

public record GradeResponse(
        Long id,
        String name,
        Byte pointRate,
        int upgradeStandardAmount
) {
    public static GradeResponse from(Grade entity){
        return new GradeResponse(
                entity.getId(),
                entity.getName(),
                entity.getPointRate(),
                entity.getUpgradeStandardAmount()
        );
    }
}