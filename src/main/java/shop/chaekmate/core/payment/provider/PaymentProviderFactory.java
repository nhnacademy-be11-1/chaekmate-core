package shop.chaekmate.core.payment.provider;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.exception.NotFoundPaymentMethodException;

@Component
@RequiredArgsConstructor
public class PaymentProviderFactory {

    private final List<PaymentProvider> providers;

    //enum에 등록된 결제 type
    public PaymentProvider getProvider(PaymentMethodType paymentType) {
        return providers.stream()
                .filter(provider -> provider.getType() == paymentType)
                .findFirst()
                .orElseThrow(NotFoundPaymentMethodException::new);
    }
}
