package shop.chaekmate.core.order.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;

@Slf4j
@Service
public class OrderService {

    public void saveOrder(PaymentApproveResponse paymentApproveResponse) {
        log.info("[ORDER] 결제 승인으로 주문 생성 - orderId={}, amount={}",
                paymentApproveResponse.orderNumber(), paymentApproveResponse.totalAmount());

        // 실제로는 Order 엔티티 생성 + 저장
    }
}
