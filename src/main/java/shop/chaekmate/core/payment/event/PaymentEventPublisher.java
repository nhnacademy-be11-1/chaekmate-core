package shop.chaekmate.core.payment.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.payment.dto.response.*;
import shop.chaekmate.core.payment.dto.response.impl.PaymentAbortedResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishPaymentApproved(PaymentApproveResponse response) {
        publisher.publishEvent(new PaymentApprovedEvent(response));
    }

    public void publishPaymentAborted(String orderNumber, PaymentAbortedResponse response) {
        publisher.publishEvent(new PaymentAbortedEvent(orderNumber, response));
    }

    public void publishPaymentCanceled(PaymentCancelResponse response) {
        publisher.publishEvent(new PaymentCanceledEvent(response));
    }
}
