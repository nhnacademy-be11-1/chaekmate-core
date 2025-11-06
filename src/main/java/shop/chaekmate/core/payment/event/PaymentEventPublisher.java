package shop.chaekmate.core.payment.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.payment.dto.response.*;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishPaymentApproved(PaymentApproveResponse response) {
        publisher.publishEvent(new PaymentApprovedEvent(this, response));
    }

    public void publishPaymentFailed(PaymentFailResponse response) {
        publisher.publishEvent(new PaymentFailedEvent(this, response));
    }
}
