package shop.chaekmate.core.payment.provider;

import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.entity.type.PaymentType;

public interface PaymentProvider {

    PaymentType getType();

    PaymentApproveResponse approve(PaymentApproveRequest request);

    PaymentCancelResponse cancel(PaymentCancelRequest request);
}
