package shop.chaekmate.core.coupon.entity.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CouponType {
    WELCOME("웰컴"),
    BIRTHDAY("생일"),
    BOOK("도서"),
    CATEGORY("카테고리"),
    ;

    private final String name;
}
