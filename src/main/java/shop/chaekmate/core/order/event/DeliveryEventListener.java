package shop.chaekmate.core.order.event;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.payment.client.DoorayMessageType;
import shop.chaekmate.core.payment.service.DoorayService;

@Component
@Slf4j
@RequiredArgsConstructor
public class DeliveryEventListener {

    private final DoorayService doorayService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShippingStarted(ShippingStartedEvent event) {
        String message = event.orderNumber()+ " - 『상품: " + event.bookTitle() + "』";

        doorayService.sendMessage(
                message,
                List.of(DoorayMessageType.SHIPPING_START)
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onShippingCompleted(ShippingCompletedEvent event) {
        doorayService.sendMessage(
                event.orderNumber(),
                List.of(DoorayMessageType.SHIPPING_COMPLETE)
        );
    }
}
