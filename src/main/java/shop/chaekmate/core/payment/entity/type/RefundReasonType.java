package shop.chaekmate.core.payment.entity.type;

import lombok.Getter;

@Getter
public enum RefundReasonType {
    CHANGE_OF_MIND(true),      // 단순변심 → 회수비 부과
    ORDER_MISTAKE(true),       // 고객 주문 실수 → 회수비 부과
    DELIVERY_FAILURE(true),    // 부재, 주소오류 → 회수비 부과

    DAMAGED_GOODS(false),      // 파본/파손 → 배송비 무료
    WRONG_DELIVERY(false);     // 오배송 → 배송비 무료

    private final boolean customerFault; // 고객 귀책 여부

    RefundReasonType(boolean customerFault) {
        this.customerFault = customerFault;
    }
}
