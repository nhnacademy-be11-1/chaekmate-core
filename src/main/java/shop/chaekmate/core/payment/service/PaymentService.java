package shop.chaekmate.core.payment.service;

import static java.time.temporal.ChronoUnit.DAYS;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;
import shop.chaekmate.core.order.dto.request.ReturnBooksRequest;
import shop.chaekmate.core.order.dto.response.ReturnBooksResponse;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.entity.Order;
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
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.RefundReasonType;
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

            // 오류 응답 반환
            return new PaymentAbortedResponse(error[0], error[1], LocalDateTime.now());
        }
    }

    @Transactional
    public PaymentCancelResponse cancel(Long memberId, PaymentCancelRequest request) {
        // 결제사 취소 API 연동 취소 시 결제 키 필요
        log.info("[결제 취소 요청] 주문번호={}, 금액={}, 사유={}, 포인트{}",
                request.orderNumber(), request.cancelAmount(), request.cancelReason(), request.pointCancelAmount());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        // 취소 품목
        List<OrderedBook> cancelBooks = findBooks(request.canceledBooks());

        // 취소 사유 입력
        updateReason(cancelBooks, request.cancelReason());

        // 취소 금액(현금/포인트) 계산
        CancelAmountResult cancelTotal = calculateAmounts(cancelBooks);
        // 현금 행 합
        long cancelCash = cancelTotal.cancelCash();
        // 포인트 행 합
        int cancelPoint = cancelTotal.cancelPoint();

        // 결제 시점 배송비
        DeliveryPolicy policy = getPolicy(payment.getCreatedAt());
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

    // 사용자 요청
    @Transactional
    public ReturnBooksResponse requestRefund(ReturnBooksRequest request){
        List<OrderedBook> returnBooks = findBooks(request.refundBooks());

        LocalDateTime requestedAt = LocalDateTime.now();
        validateRefundPeriod(returnBooks, request.refundReason(), requestedAt);

        // 사유
        updateReason(returnBooks, request.refundReason().name());

        // 상태 변경
        orderService.applyOrderReturnRequest(request.orderNumber(), returnBooks);

        //예상 환불 금액 계산
        CancelAmountResult amounts = calculateAmounts(returnBooks);
        long refundCash = amounts.cancelCash();
        int refundPoint = amounts.cancelPoint();

        //현재 배송 정책
        DeliveryPolicy policy = getPolicy(requestedAt);
        long returnFee = calculateReturnFee(request.refundReason(), policy);

        // 반품비 차감 (현금 → 포인트 순)
        long feeFromCash = Math.min(refundCash, returnFee);
        refundCash -= feeFromCash;

        long remainingFee = returnFee - feeFromCash;
        if (remainingFee > 0) {
            if (remainingFee > refundPoint) {
                throw new IllegalStateException("반품비가 포인트 환불 금액보다 클 수 없습니다.");
            }
            refundPoint -= (int) remainingFee;
        }

        // 아직 승인 전이므로, 여기서는 “예상 값”으로만 응답
        return new ReturnBooksResponse(
                request.orderNumber(),
                refundCash,
                refundPoint,
                returnFee,
                requestedAt,
                request.refundBooks()
        );
    }

    // 관리자 승인
//    @RequiredAdmin
    @Transactional
    public ReturnBooksResponse approveRefund(ReturnBooksRequest request){
        Order order = orderService.getOrderEntity(request.orderNumber());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> refundBooks = findBooks(request.refundBooks());

        updateReason(refundBooks, request.refundReason().name());

        CancelAmountResult amounts = calculateAmounts(refundBooks);

        // 현재 배송정책 기준 회수비 계산
        LocalDateTime refundedAt = LocalDateTime.now();

        DeliveryPolicy policy = getPolicy(refundedAt);
        long returnFee = calculateReturnFee(request.refundReason(), policy);

        // 최종 환불금액 계산
        long refundCash = amounts.cancelCash();
        int refundPoint = amounts.cancelPoint();

        // 반품비 처리 (현금 -> 포인트 순서)
        long feeFromCash = Math.min(refundCash, returnFee);
        refundCash -= feeFromCash;

        long remainingFee = returnFee - feeFromCash;
        if (remainingFee > 0) {
            if (remainingFee > refundPoint) {
                throw new IllegalStateException("반품비가 포인트 환불 금액보다 클 수 없습니다.");
            }
            refundPoint -= (int) remainingFee;
        }

        boolean isMember = order.getMember() != null;
        if (isMember) {
            // (회원) 포인트 반환
            refundPoint += (int)refundCash;
            refundCash = 0;

            payment.applyCancel(0, refundPoint);
            boolean isFullRefund = isAllRefund(order, refundBooks);

            if (isFullRefund) {
                // 전체 반품
                paymentHistoryRepository.save(
                        PaymentHistory.canceled(
                                payment,
                                refundPoint,
                                "REFUND_" + request.refundReason().name(),
                                refundedAt
                        )
                );
            } else {
                // 부분 반품
                paymentHistoryRepository.save(
                        PaymentHistory.partialCanceled(
                                payment,
                                refundPoint,
                                "REFUND_" + request.refundReason().name(),
                                refundedAt
                        )
                );
            }
        }
        else {
            // (비회원) 취소로 현금 환불
            PaymentCancelRequest pgRequest = new PaymentCancelRequest(
                    payment.getPaymentKey(),
                    request.orderNumber(),
                    "REFUND_:"+request.refundReason().name(),
                    refundCash,     //현금 환불 금액
                    0,
                    request.refundBooks()
            );

            PaymentProvider provider = providerFactory.getProvider(payment.getPaymentType());
            provider.cancel(pgRequest);
        }

        orderService.applyOrderReturn(request.orderNumber(), refundBooks);

        ReturnBooksResponse response = new ReturnBooksResponse(
                request.orderNumber(),
                refundCash,
                refundPoint,
                returnFee,
                refundedAt,
                request.refundBooks()
        );
        eventPublisher.publishRefundApproved(response);

        return response;
    }

    private List<OrderedBook> findBooks(List<CanceledBooksRequest> books) {
        return orderedBookRepository.findAllById(
                books.stream().map(CanceledBooksRequest::orderedBookId).toList()
        );
    }

    // 사유 입력
    private void updateReason(List<OrderedBook> books, String reason) {
        books.forEach(book -> book.updateReason(reason));
    }

    // 취소 총 가격(현금/포인트) 계산
    private CancelAmountResult calculateAmounts(List<OrderedBook> cancelBooks) {
        long cancelCash = cancelBooks.stream()
                .mapToLong(OrderedBook::getTotalPrice)
                .sum();

        int cancelPoint = cancelBooks.stream()
                .mapToInt(OrderedBook::getPointUsed)
                .sum();

        return new CancelAmountResult(cancelCash, cancelPoint);
    }

    //남은 금액 계산
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

    //배송정책 찾기
    private DeliveryPolicy getPolicy(LocalDateTime paymentAt) {
        return deliveryPolicyRepository.findPolicyAt(paymentAt)
                .orElseThrow(NotFoundDeliveryPolicyException::new);
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

    // 환불 타입 배송비 체크
    private long calculateReturnFee(RefundReasonType reason, DeliveryPolicy policy) {
        return reason.isCustomerFault() ? policy.getDeliveryFee() : 0;
    }

    // 남은 품목 확인
    private boolean isAllRefund(Order order, List<OrderedBook> refundBooks) {
        return order.getOrderedBooks().size() == refundBooks.size();
    }

    // 출고일 기준 체크
    private void validateRefundPeriod(List<OrderedBook> refundBooks, RefundReasonType reason, LocalDateTime refundedAt) {

        for (OrderedBook ob : refundBooks) {
            LocalDateTime shippedAt = ob.deliveredAt(); // 배송 도착

            if (shippedAt == null) {
                throw new IllegalStateException("배송 이력이 없는 상품은 반품할 수 없습니다.");
            }

            long days = DAYS.between(
                    shippedAt.toLocalDate(), refundedAt.toLocalDate()
            );

            if (reason.isCustomerFault() && days > 10) {
                throw new IllegalStateException("고객 귀책 사유 반품은 출고일 기준 10일 이내에만 가능합니다.");
            }

            if (!reason.isCustomerFault() && days > 30) {
                throw new IllegalStateException("상품 파손/오배송 반품은 출고일 기준 30일 이내에만 가능합니다.");
            }
        }
    }

}
