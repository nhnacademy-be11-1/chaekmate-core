package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.service.OrderService;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.base.PaymentResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentAbortedResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProviderFactory providerFactory;
    private final PaymentEventPublisher eventPublisher;
    private final PaymentErrorService paymentErrorService;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final OrderService orderService;


    public PaymentResponse approve(Long memberId, PaymentApproveRequest request) {
        log.info("[결제 승인 요청] 주문번호={}, 결제수단={}, 결제금액={}, 포인트사용={}",
                request.orderNumber(), request.paymentType(), request.amount(), request.pointUsed());

        PaymentProvider provider = providerFactory.getProvider(request.paymentType());

        try {
            orderService.verifyOrderStock(request.orderNumber());

            PaymentApproveResponse response = provider.approve(request);

            orderService.applyPaymentSuccess(response.orderNumber());

            // 이벤트 발행
            eventPublisher.publishPaymentApproved(response);
            log.info("[결제 승인 완료 및 이벤트 발행] 주문번호={}, 상태={}", response.orderNumber(), response.status());

            return response;

        } catch (Exception e) {
            log.error("[결제 승인 실패] 주문번호={}, 사유={}", request.orderNumber(), e.getMessage());

            String msg = e.getMessage();
            if (msg == null || !msg.contains(":")) {
                msg = "UNKNOWN:" + (msg == null ? "결제 요청 중 오류가 발생했습니다." : msg);
            }

            String[] error = msg.split(":", 2);
            //실패 로그 저장 - 새 트랜잭션으로 분리
            paymentErrorService.saveAbortedPayment(request, msg);

            // 오류 응답 생성
            PaymentAbortedResponse response = new PaymentAbortedResponse(error[0], error[1], LocalDateTime.now());

            // 이벤트 발행
            eventPublisher.publishPaymentAborted(request.orderNumber(), response);
            log.info("[결제 실패 완료 및 이벤트 발행] 주문번호={}, 에러코드={}", request.orderNumber(), error[0]);

            return response;
        }
    }

    @Transactional
    public PaymentCancelResponse cancel(Long memberId, PaymentCancelRequest request) {
        // 결제사 취소 API 연동 취소 시 결제 키 필요(현재는 x)
        log.info("[결제 취소 요청] 주문번호={}, 금액={}, 사유={}",
                request.orderNumber(), request.cancelAmount(), request.cancelReason());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        LocalDateTime now = LocalDateTime.now();

        long canceledAmount = payment.cancelOrPartial(request.cancelAmount());

        PaymentHistory history = PaymentHistory.canceled(payment, canceledAmount, request.cancelReason(), now);
        paymentHistoryRepository.save(history);

        PaymentCancelResponse response = new PaymentCancelResponse(
                payment.getOrderNumber(),
                request.cancelReason(),
                canceledAmount,
                now,
                request.canceledBooks()
        );

        eventPublisher.publishPaymentCanceled(response);
        log.info("[ 결제 취소 완료 및 이벤트 발행 ] 주문번호={}, 취소금액={}", response.orderNumber(), response.canceledAmount());

        return response;
    }
}
