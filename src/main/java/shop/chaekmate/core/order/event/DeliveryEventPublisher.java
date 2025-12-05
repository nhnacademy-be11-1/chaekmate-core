package shop.chaekmate.core.order.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishShippingStarted(ShippingStartedEvent event) {
        publisher.publishEvent(event);
    }

    public void publishShippingCompleted(ShippingCompletedEvent event) {
        publisher.publishEvent(event);
    }

}
