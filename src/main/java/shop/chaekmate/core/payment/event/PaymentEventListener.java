package shop.chaekmate.core.payment.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.order.service.OrderService;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentFailResponse;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;

    @EventListener
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse res = event.getApproveResponse();
        log.info("[EVENT] 결제 승인 수신 - 주문ID: {}", res.orderNumber());

        orderService.saveOrder(res);
    }

    @EventListener
    public void handlePaymentFailed(PaymentFailedEvent event) {
        PaymentFailResponse res = event.getFailResponse();
        log.warn("[EVENT] 결제 실패 수신 - 주문ID: {}, 사유: {}", res.orderNumber(), res.message());
    }
}
