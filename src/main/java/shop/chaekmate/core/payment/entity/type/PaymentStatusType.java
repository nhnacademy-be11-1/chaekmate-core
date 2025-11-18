package shop.chaekmate.core.payment.entity.type;

public enum PaymentStatusType {
    APPROVED,           // 승인
    ABORTED,            // 실패
    CANCELED,           // 취소
    PARTIAL_CANCELED    // 부분 취소
    ;
}
