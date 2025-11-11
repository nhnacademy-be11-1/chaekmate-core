package shop.chaekmate.core.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.order.service.OrderService;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse res = event.approveResponse();
        log.info("[EVENT] 결제 승인 수신 - 주문ID: {}", res.orderNumber());
        
        //주문 로직 작성
        orderService.saveOrder(res);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentFailed(PaymentCanceledEvent event) {
        PaymentCancelResponse res = event.cancelResponse();
        log.info("[EVENT] 결제 취소 수신 - 주문ID: {}", res.orderNumber());
        // 주문 상태 변경
    }
}
