package shop.chaekmate.core.payment.dto.request;

public record CancelAmountResult(
        long cancelCash,   // 취소 대상 금액
        int cancelPoint     // 취소 대상 포인트
) {
}