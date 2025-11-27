package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.exception.NotFoundDeliveryPolicyException;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepository;
import shop.chaekmate.core.order.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
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

            // 오류 응답 반환
            return new PaymentAbortedResponse(error[0], error[1], LocalDateTime.now());
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
        List<OrderedBook> cancelBooks = canceledBooks(request);

        // 사유 입력
        cancelBooks.forEach(ob -> ob.updateReason(request.cancelReason()));

        CancelAmountResult cancelTotal = calculateCancelAmounts(cancelBooks);
        // 결제된 현금 행 합
        long cancelCash = cancelTotal.cancelCash();
        // 결제된 포인트 행 합
        int cancelPoint  = cancelTotal.cancelPoint();

        // 남은 주문 금액 계산
        long remainAmount = calculateRemainAmount(payment.getOrderNumber(), cancelCash+cancelPoint);

        boolean fullCancel = (remainAmount == 0);

        long cashCancelAmount;
        int pointCancelAmount;

        if (fullCancel) {
            // 전체 취소
            cashCancelAmount = payment.getTotalAmount();
            pointCancelAmount = payment.getPointUsed();
        } else {
            // 부분 취소 (배송비 반영)
            DeliveryPolicy policy = deliveryPolicyRepository.findPolicyAt(payment.getCreatedAt())
                    .orElseThrow(NotFoundDeliveryPolicyException::new);

            long adjustedAmount = applyDeliveryPolicy(payment, remainAmount, policy, cancelCash+cancelPoint);

            // 현금 취소 금액 계산
            cashCancelAmount = Math.min(payment.getTotalAmount(), adjustedAmount);

            // 포인트 취소 금액 계산
            pointCancelAmount = (int) (adjustedAmount - cashCancelAmount);
        }

        // provider로 넘길 최종 요청
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

        eventPublisher.publishPaymentCanceled(response);
        log.info("[ 결제 취소 완료 및 이벤트 발행 ] 주문번호={}, 취소금액={}, 취소 포인트={}", response.orderNumber(), response.canceledCash(),
                response.canceledPoint());

        return response;
    }

    private List<OrderedBook> canceledBooks(PaymentCancelRequest request) {
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


    private long calculateRemainAmount(String orderNumber, long cancelItemsTotal) {

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(NotFoundOrderNumberException::new);

        // 남은 결제 금액 (배송비 포함)
        long remainAmount = order.getTotalPrice() - cancelItemsTotal;

        if (remainAmount < 0) {
            throw new IllegalStateException("취소 금액이 주문 금액을 초과합니다.");
        }

        return remainAmount;
    }

    private long applyDeliveryPolicy(Payment payment, long remainAmount, DeliveryPolicy policy, long cancelItemsTotal) {
        // 이미 한 번 배송비가 깨져서 제외된 적이 있다면, 이번 취소에서는 배송비 계산 X
        if (payment.isDeliveryFeeAdjusted()) {
            return cancelItemsTotal;
        }

        // 이번 취소로 인해 남은 금액이 무료배송 기준보다 작아짐 -> 배송비 제외해야 함
        if (remainAmount < policy.getFreeStandardAmount()) {

            long adjusted = (cancelItemsTotal - policy.getDeliveryFee());

            if (adjusted < 0) {
                adjusted = 0;
            }

            // 배송비 조정은 한 번만
            payment.markDeliveryFeeAdjusted();

            return adjusted;
        }

        // 여전히 무료배송 조건 유지 -> 배송비 제외 안 함
        return cancelItemsTotal;
    }
}
