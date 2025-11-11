package shop.chaekmate.core.payment.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;

@Getter
public class PaymentApprovedEvent extends ApplicationEvent {

    private final PaymentApproveResponse approveResponse;

    public PaymentApprovedEvent(Object source, PaymentApproveResponse approveResponse) {
        super(source);
        this.approveResponse = approveResponse;
    }
}
