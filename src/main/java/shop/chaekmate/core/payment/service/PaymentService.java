package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.exception.NotFoundDeliveryPolicyException;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.order.service.OrderService;
import shop.chaekmate.core.payment.dto.request.CancelAmountResult;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.base.PaymentResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentAbortedResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentProviderFactory providerFactory;
    private final PaymentEventPublisher eventPublisher;
    private final PaymentErrorService paymentErrorService;
    private final PaymentRepository paymentRepository;
    private final DeliveryPolicyRepository deliveryPolicyRepository;
    private final OrderedBookRepository orderedBookRepository;
    private final OrderService orderService;

    @Transactional
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
        // 결제사 취소 API 연동 취소 시 결제 키 필요
        log.info("[결제 취소 요청] 주문번호={}, 금액={}, 사유={}",
                request.orderNumber(), request.cancelAmount(), request.cancelReason());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        // 취소 품목
        List<OrderedBook> cancelBooks = cancelBooks(request);
        // 취소 사유 입력
        cancelBooks.forEach(ob -> ob.updateReason(request.cancelReason()));

        // 취소 금액(현금/포인트) 계산
        CancelAmountResult cancelTotal = calculateCancelAmounts(cancelBooks);
        // 현금 행 합
        long cancelCash = cancelTotal.cancelCash();
        // 포인트 행 합
        int cancelPoint = cancelTotal.cancelPoint();

        DeliveryPolicy policy = deliveryPolicyRepository.findPolicyAt(payment.getCreatedAt())
                .orElseThrow(NotFoundDeliveryPolicyException::new);

        // 결제 시점 배송비
        long deliveryFee = policy.getDeliveryFee();

        // 남은 주문 금액 계산
        long remainAmount = calculateRemainAmount(payment, cancelCash, cancelPoint, deliveryFee);

        long cashCancelAmount;
        int pointCancelAmount;

        // 전체 취소
        if (remainAmount == 0) {
            cashCancelAmount = payment.getTotalAmount();
            pointCancelAmount = payment.getPointUsed();
        }
        // 부분 취소
        else {
            // 이번 취소에서 더 빼야 할 배송비 (0 or deliveryFee)
            long extraDeliveryFee = getAdditionalDeliveryFee(payment, remainAmount, policy);

            // 기본값 현금/포인트 전액 환불
            cashCancelAmount = cancelCash;
            pointCancelAmount = cancelPoint;

            if (extraDeliveryFee > 0) {
                // 현금 먼저 배송비 차감
                long feeFromCash = Math.min(cashCancelAmount, extraDeliveryFee);
                cashCancelAmount -= feeFromCash;

                // 남은 배송비 포인트에서 차감
                long remainFee = extraDeliveryFee - feeFromCash;
                if (remainFee > 0) {
                    if (remainFee > pointCancelAmount) {
                        throw new IllegalStateException("배송비가 취소 포인트보다 클 수 없습니다.");
                    }
                    pointCancelAmount -= (int) remainFee;
                }
            }
        }

        PaymentCancelRequest finalRequest = new PaymentCancelRequest(
                payment.getPaymentKey(),
                request.orderNumber(),
                request.cancelReason(),
                cashCancelAmount,
                pointCancelAmount,
                request.canceledBooks()
        );

        PaymentProvider provider = providerFactory.getProvider(payment.getPaymentType());

        PaymentCancelResponse response = provider.cancel(finalRequest);

        orderService.applyOrderCancel(response);

        //취소 이벤트 발행
        eventPublisher.publishPaymentCanceled(response);
        log.info("[ 결제 취소 완료 및 이벤트 발행 ] 주문번호={}, 취소금액={}, 취소 포인트={}", response.orderNumber(), response.canceledCash(),
                response.canceledPoint());

        return response;
    }

    private List<OrderedBook> cancelBooks(PaymentCancelRequest request) {
        return orderedBookRepository.findAllById(
                request.canceledBooks()
                        .stream()
                        .map(CanceledBooksRequest::orderedBookId)
                        .toList()
        );
    }

    private CancelAmountResult calculateCancelAmounts(List<OrderedBook> cancelBooks) {
        long cancelCash = cancelBooks.stream()
                .mapToLong(OrderedBook::getTotalPrice)
                .sum();

        int cancelPoint = cancelBooks.stream()
                .mapToInt(OrderedBook::getPointUsed)
                .sum();

        return new CancelAmountResult(cancelCash, cancelPoint);
    }


    private long calculateRemainAmount(Payment payment, long cancelCash, int cancelPoint, long deliveryFee) {
        long remainCash = payment.getTotalAmount() - cancelCash;
        int remainPoint = payment.getPointUsed() - cancelPoint;

        long remainAmount = remainCash + remainPoint;

        // 남은 금액 = 배송비 => 전체취소 처리
        if (remainAmount == deliveryFee) {
            return 0;
        }

        // 배송비가 이미 한 번 빠진 상태라면
        if (payment.isDeliveryFeeAdjusted() && remainAmount <= deliveryFee) {
            return 0;
        }

        return remainAmount;
    }

    // 취소 시 남은 결제 금액 무료배송 대상인지 체크 후 배송비 포함 여부
    private long getAdditionalDeliveryFee(Payment payment, long remainAmount, DeliveryPolicy policy) {

        // 이미 한 번 배송비 조정 했으면 더 이상 안 뺌
        if (payment.isDeliveryFeeAdjusted()) {
            return 0L;
        }

        // 이번 취소 후 남은 금액이 무료배송 기준보다 작으면 → 배송비 한 번 차감
        if (remainAmount < policy.getFreeStandardAmount()) {
            payment.markDeliveryFeeAdjusted();
            return policy.getDeliveryFee();
        }

        // 여전히 무료배송 기준 충족 -> 배송비 차감 없음
        return 0L;
    }
}
