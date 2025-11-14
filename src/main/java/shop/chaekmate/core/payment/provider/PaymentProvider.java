package shop.chaekmate.core.payment.provider;

import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

public interface PaymentProvider {

    PaymentMethodType getType();

    PaymentApproveResponse approve(PaymentApproveRequest request);

}
