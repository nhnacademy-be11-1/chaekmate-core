package shop.chaekmate.core.order.entity.type;

public enum OrderStatusType {

    WAITING, // 대기
    SHIPPING, // 배송중
    DELIVERED, // 배송완료
    RETURNED, // 반품
    CANCELED, // 취소
    ;
}
