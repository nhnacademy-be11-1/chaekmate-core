package shop.chaekmate.core.payment.provider;

import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

public interface PaymentProvider {

    PaymentMethodType getType();

    PaymentApproveResponse approve(PaymentApproveRequest request);

    PaymentCancelResponse cancel(PaymentCancelRequest request);

    //환불 로직
    // PaymentRefundResponse refund(PaymentRefundRequest request);
}
