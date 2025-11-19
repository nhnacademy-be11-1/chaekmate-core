package shop.chaekmate.core.point.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.event.PaymentApprovedEvent;
import shop.chaekmate.core.point.service.PointEarnService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointEarnService pointEarnService;

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse response = event.approveResponse();
        log.info("[포인트 이벤트] 결제 승인 이벤트 수신 - 주문번호: {}, 금액: {}, 사용포인트: {}",
                response.orderNumber(), response.totalAmount());

        try {
            // 포인트 적립 처리
            pointEarnService.earnPointForOrder(
                    response.orderNumber(),
                    response.totalAmount()
            );

            log.info("[포인트 이벤트] 포인트 적립 완료 - 주문번호: {}", response.orderNumber());

        } catch (Exception e) {
            // 포인트 적립 실패 시 로그만 남기고 예외를 던지지 않음
            // - 결제는 이미 완료되었으므로 포인트 적립 실패가 결제를 롤백하면 안 됨
            log.error("[포인트 이벤트] 포인트 적립 실패 - 주문번호: {}, 오류: {}",
                    response.orderNumber(), e.getMessage(), e);
        }
    }
}

