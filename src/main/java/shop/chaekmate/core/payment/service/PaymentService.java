package shop.chaekmate.core.payment.service;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.*;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProviderFactory providerFactory;
    private final PaymentEventPublisher eventPublisher;
    private final PaymentHistoryRepository paymentHistoryRepository;


    public PaymentApproveResponse approve(PaymentApproveRequest request) {
        PaymentProvider provider = providerFactory.getProvider(request.paymentType());
        PaymentApproveResponse response;

        try {
            response = provider.approve(request);

            saveApprovedPayment(request, response);

            eventPublisher.publishPaymentApproved(response);
            return response;

        } catch (Exception e) {
            saveFailedPayment(request, e.getMessage());

            var failResponse = new PaymentFailResponse(
                    request.orderId(),
                    e.getMessage(),
                    request.amount(),
                    "FAILED",
                    OffsetDateTime.now()
            );
            eventPublisher.publishPaymentFailed(failResponse);

            throw e;
        }
    }

    @Transactional
    protected void saveApprovedPayment(PaymentApproveRequest request, PaymentApproveResponse response) {
        PaymentHistory approved = PaymentHistory.createApproved(
                response.orderId(),
                request.paymentType().name(),
                response.paymentKey(),
                response.totalAmount(),
                OffsetDateTime.now()
        );
        log.info("payment history 승인 저장 완료 {} {} {} {} {}",
                approved.getOrderNumber(),
        approved.getPaymentType(),
        approved.getPaymentKey(),
        approved.getTotalAmount(),
        approved.getPaymentStatus(),
        approved.getApprovedAt());
//        paymentHistoryRepository.save(approved);
    }

    /**
     * 실패 저장 (트랜잭션 분리)
     */
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Transactional
    protected void saveFailedPayment(PaymentApproveRequest request, String reason) {
        PaymentHistory failed = PaymentHistory.createFailed(
                request.orderId(),
                request.paymentType().name(),
                request.amount(),
                OffsetDateTime.now(),
                reason
        );

        log.info("payment history 실패 저장 완료 {} {} {} {} {}",
                failed.getOrderNumber(),
                failed.getPaymentType(),
                failed.getTotalAmount(),
                failed.getPaymentStatus(),
                failed.getFailedAt(),
                failed.getReason());

//        paymentHistoryRepository.save(failed);
    }

    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        return null;
    }
}
