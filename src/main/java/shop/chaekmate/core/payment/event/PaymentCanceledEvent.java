package shop.chaekmate.core.payment.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;

@Getter
public class PaymentCanceledEvent extends ApplicationEvent {

    private final PaymentCancelResponse cancelResponse;

    public PaymentCanceledEvent(Object source, PaymentCancelResponse cancelResponse) {
        super(source);
        this.cancelResponse = cancelResponse;
    }
}
