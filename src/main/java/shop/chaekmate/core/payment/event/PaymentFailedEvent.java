package shop.chaekmate.core.payment.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import shop.chaekmate.core.payment.dto.response.PaymentFailResponse;

@Getter
public class PaymentFailedEvent extends ApplicationEvent {

    private final PaymentFailResponse failResponse;

    public PaymentFailedEvent(Object source, PaymentFailResponse failResponse) {
        super(source);
        this.failResponse = failResponse;
    }
}
