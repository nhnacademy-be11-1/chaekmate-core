package shop.chaekmate.core.payment.provider.impl;

import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointPaymentProvider implements PaymentProvider {

    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.POINT;
    }

    @Transactional
    @Override
    public PaymentApproveResponse approve(PaymentApproveRequest request) {
        log.info("[POINT] 결제 승인 요청 - 주문번호={}, 포인트결제금액={}", request.orderNumber(), request.pointUsed());

        Payment payment = Payment.createApproved(
                request.orderNumber(),
                null,
                getType(),
                0L,
                request.pointUsed()
        );
        paymentRepository.save(payment);
        OffsetDateTime now = OffsetDateTime.now();
        paymentHistoryRepository.save(PaymentHistory.approved(payment, request.pointUsed(), now));

        return new PaymentApproveResponse(
                request.orderNumber(),
                0L,
                request.pointUsed(),
                PaymentStatusType.APPROVED.name(),
                now
        );
    }
}
