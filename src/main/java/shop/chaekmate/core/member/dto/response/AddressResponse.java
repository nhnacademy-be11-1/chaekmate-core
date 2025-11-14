package shop.chaekmate.core.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import shop.chaekmate.core.member.entity.MemberAddress;

@Getter
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String memo;
    private String streetName;
    private String detail;
    private int zipcode;

    public static AddressResponse from(MemberAddress entity) {
        return new AddressResponse(
                entity.getId(),
                entity.getMemo(),
                entity.getStreetName(),
                entity.getDetail(),
                entity.getZipcode()
        );
    }
}
