package shop.chaekmate.core.payment.entity.type;

import lombok.Getter;

@Getter
public enum ReturnReasonType {
    //반품비 부과
    CHANGE_OF_MIND(true),      // 단순변심
    ORDER_MISTAKE(true),       // 고객 주문 실수
    DELIVERY_FAILURE(true),    // 부재, 주소오류

    //반품비 무료
    DAMAGED_GOODS(false),      // 파본/파손
    WRONG_DELIVERY(false);     // 오배송

    private final boolean customerFault; // 고객 귀책 여부

    ReturnReasonType(boolean customerFault) {
        this.customerFault = customerFault;
    }
}
