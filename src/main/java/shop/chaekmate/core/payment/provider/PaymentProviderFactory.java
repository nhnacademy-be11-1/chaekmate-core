package shop.chaekmate.core.payment.provider;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.payment.entity.type.PaymentType;
import shop.chaekmate.core.payment.exception.PaymentMethodNotFoundException;

@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final List<PaymentProvider> providers;

    public PaymentProvider getProvider(PaymentType paymentType) {
        return providers.stream()
                .filter(provider -> provider.getType() == paymentType)
                .findFirst()
                .orElseThrow(PaymentMethodNotFoundException::new);
    }
}
