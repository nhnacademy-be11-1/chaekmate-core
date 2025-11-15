package shop.chaekmate.core.member.dto.response;

import shop.chaekmate.core.member.entity.MemberAddress;

public record AddressResponse(
    Long id,
    String memo,
    String streetName,
    String detail,
    int zipcode
){ public static AddressResponse from(MemberAddress entity) {
        return new AddressResponse(
                entity.getId(),
                entity.getMemo(),
                entity.getStreetName(),
                entity.getDetail(),
                entity.getZipcode()
        );
    }
}
