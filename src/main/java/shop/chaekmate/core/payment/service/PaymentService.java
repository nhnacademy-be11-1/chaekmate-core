package shop.chaekmate.core.payment.service;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.*;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProviderFactory providerFactory;

    private final PaymentEventPublisher eventPublisher;

    private final PaymentRepository paymentRepository;

    private final PaymentHistoryRepository paymentHistoryRepository;

    private final PaymentErrorService paymentErrorService;

    @Transactional
    public PaymentApproveResponse approve(PaymentApproveRequest request) {
        log.info("[결제 승인 요청] 주문번호={}, 결제수단={}, 결제금액={}, 포인트사용={}",
                request.orderNumber(), request.paymentType(), request.amount(),request.pointUsed());

        if (request.amount() == 0) {
            log.info("[포인트로만 결제 처리] 주문번호={} 포인트결제금액={}", request.orderNumber(),  request.pointUsed());
            Payment payment = Payment.createApproved(
                    request.orderNumber(),
                    null,
                    PaymentMethodType.POINT,
                    0L,
                    request.pointUsed()
            );
            paymentRepository.save(payment);

            PaymentHistory history = PaymentHistory.approved(payment, request.pointUsed(), OffsetDateTime.now());
            paymentHistoryRepository.save(history);

            PaymentApproveResponse response = new PaymentApproveResponse(
                    request.orderNumber(),
                    null,
                    request.pointUsed(),
                    PaymentStatusType.APPROVED.name(),
                    OffsetDateTime.now()
            );

            eventPublisher.publishPaymentApproved(response);
            log.info("[포안트 결제 승인 이벤트 발행] 주문번호={}, 상태={}", response.orderNumber(), response.status());

            return response;
        }


        PaymentProvider provider = providerFactory.getProvider(request.paymentType());

        try {
            PaymentApproveResponse response = provider.approve(request);
            log.info("[결제 승인 완료] 주문번호={}, 결제키={}, 승인금액={}",
                    response.orderNumber(), response.paymentKey(), response.totalAmount());

            Payment payment = Payment.createApproved(
                    request.orderNumber(),
                    response.paymentKey(),
                    request.paymentType(),
                    response.totalAmount(),
                    request.pointUsed()
            );
            paymentRepository.save(payment);

            // 포인트 사용량 같이 저장
            PaymentHistory history = PaymentHistory.approved(payment, response.totalAmount()+Optional.ofNullable(request.pointUsed()).orElse(0), OffsetDateTime.now());
            paymentHistoryRepository.save(history);

            log.info("[결제 승인 저장 완료] 주문번호={}, 결제금액={}, 포인트사용={}",
                    payment.getOrderNumber(), payment.getTotalAmount(), payment.getPointUsed());

            // 이벤트 발행 -> 주문 서비스 이동
            eventPublisher.publishPaymentApproved(response);
            log.info("[결제 승인 이벤트 발행] 주문번호={}, 상태={}", response.orderNumber(), response.status());

            return response;

        } catch (Exception e) {
            log.error("[결제 승인 실패] 주문번호={}, 사유={}", request.orderNumber(), e.getMessage());

            //실패 로그 저장 - 새 트랜잭션으로 분리
            paymentErrorService.saveAbortedPayment(request, e.getMessage());

            return new PaymentApproveResponse(
                    request.orderNumber(),
                    e.getMessage(),
                    request.amount(),
                    PaymentStatusType.ABORTED.name(),
                    OffsetDateTime.now()
            );
        }
    }

    @Transactional
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        log.info("[결제 취소 요청] 주문번호={}, 결제키={}, 금액={}, 사유={}",
                request.orderNumber(), request.paymentKey(), request.cancelAmount(), request.cancelReason());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        PaymentProvider provider = providerFactory.getProvider(payment.getPaymentType());

        if (payment.getPaymentType() == PaymentMethodType.POINT) {
            log.info("[포인트 결제 취소] 주문번호={}, 금액={}", payment.getOrderNumber(), payment.getPointUsed());

            // 결제 상태 취소로 변경
            payment.cancel();

            long cancelAmount = payment.getPointUsed();
            PaymentHistory history = PaymentHistory.canceled(payment, cancelAmount, request.cancelReason(), OffsetDateTime.now());
            paymentHistoryRepository.save(history);

            PaymentCancelResponse response = new PaymentCancelResponse(
                    payment.getOrderNumber(),
                    request.cancelReason(),
                    cancelAmount,
                    PaymentStatusType.CANCELED.name(),
                    OffsetDateTime.now()
            );

            eventPublisher.publishPaymentCanceled(response);
            log.info("[포인트 결제 취소 이벤트 발행] 주문번호={}, 상태={}", response.orderNumber(), response.status());

            return response;
        }

        try {
            PaymentCancelResponse response = provider.cancel(request);
            log.info("[결제 취소 성공] 주문번호={}, 취소금액={}",
                    request.orderNumber(), response.canceledAmount());

            // 결제 상태 취소로 변경
            payment.cancel();

            long cancelAmount = Optional.ofNullable(request.cancelAmount()).orElse(payment.getTotalAmount());
            PaymentHistory history = PaymentHistory.canceled(payment, cancelAmount, request.cancelReason(),
                    OffsetDateTime.now());
            paymentHistoryRepository.save(history);

            log.info("[결제 취소 저장 완료] 주문번호={}, 취소금액={}, 사유={}",
                    request.orderNumber(), cancelAmount, request.cancelReason());

            eventPublisher.publishPaymentCanceled(response);
            log.info("[결제 취소 이벤트 발행] 주문번호={}, 상태={}", response.orderNumber(), response.status());

            return response;

        } catch (Exception e) {
            log.error("[결제 취소 실패] 주문번호={}, 사유={}", request.orderNumber(), e.getMessage());
            throw new IllegalStateException("결제 취소 실패: " + e.getMessage(), e);
        }
    }
}
