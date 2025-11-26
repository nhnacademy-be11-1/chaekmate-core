package shop.chaekmate.core.order.entity.type;

public enum OrderedBookStatusType {
    PAYMENT_READY,      // 결제전
    PAYMENT_FAILED,     // 결제실패
    PAYMENT_COMPLETE,   // 결제완료

    SHIPPING,           // 배송중
    DELIVERED,          // 배송완료

    CANCEL_REQUEST,     // 고객이 취소 요청
    CANCELED,           // 취소

    RETURN_REQUEST,     // 고객이 반품 요청
    RETURNED            // 반품
}
