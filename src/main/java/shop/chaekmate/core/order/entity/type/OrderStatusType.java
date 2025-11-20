package shop.chaekmate.core.order.entity.type;

public enum OrderStatusType {
    PAYMENT_READY,  // 결제 대기
    PAYMENT_FAILED, // 결제 실패
    WAITING,        // 대기 (결제완료 - 배송대기중)

    SHIPPING,       // 배송중
    DELIVERED,      // 배송완료

    RETURNED,       // 반품
    CANCELED,       // 취소
    ;
}
