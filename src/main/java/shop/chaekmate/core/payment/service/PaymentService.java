package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
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
import shop.chaekmate.core.payment.entity.type.ReturnReasonType;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.event.ReturnRequestedEvent;
import shop.chaekmate.core.payment.exception.ExceedCancelAmountException;
import shop.chaekmate.core.payment.exception.InvalidRefundRequestTimeException;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.exception.RefundBeforeDeliveredException;
import shop.chaekmate.core.payment.exception.RefundPeriodExceededException;
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

            orderService.applyPaymentFail(request.orderNumber());

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
        log.info("[결제 취소 요청] 주문번호={}, 금액={}, 사유={}, 포인트{}",
                request.orderNumber(), request.cancelAmount(), request.cancelReason(), request.pointCancelAmount());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        // 취소 품목
        List<OrderedBook> cancelBooks = findBooks(request.canceledBooks());

        // 취소 사유 입력
        updateReason(cancelBooks, request.cancelReason(), LocalDateTime.now());

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
                CancelAmountResult amountResult = deductFee(cashCancelAmount, cancelPoint, extraDeliveryFee);
                cashCancelAmount = amountResult.cancelCash();
                pointCancelAmount = amountResult.cancelPoint();
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
    public ReturnBooksResponse requestReturn(Long memberId, ReturnBooksRequest request){
        List<OrderedBook> returnBooks = findBooks(request.returnBooks());

        LocalDateTime requestedAt = LocalDateTime.now();

        // 사유
        updateReason(returnBooks, request.returnReason().name(), requestedAt);

        // 사유 검증
        validateReturnInfo(returnBooks, request.returnReason());

        CancelAmountResult amounts = calculateAmounts(returnBooks);
        DeliveryPolicy policy = getPolicy(requestedAt);

        long returnFee = calculateReturnFee(request.returnReason(), policy);

        // 상태 변경
        orderService.applyOrderReturnRequest(returnBooks);
        long returnCash = amounts.cancelCash();
        int returnPoint = amounts.cancelPoint();

        if(memberId != null){
            returnPoint += (int)returnCash;
            returnCash = 0;
        }
        // 결제한 현금, 포인트, 배송차감액 보여주고 (비회원시 포인트x) 검증 여부에 따른 배송비 차감 여부 (회원시 포인트로 반환 된다 명시)
        ReturnBooksResponse response = new ReturnBooksResponse(
                request.orderNumber(),
                returnCash,
                returnPoint,
                returnFee,
                requestedAt,
                request.returnBooks()
        );

        eventPublisher.publishReturnRequested(new ReturnRequestedEvent(response));

        return response;
    }

    // 관리자 승인
    @Transactional
    public ReturnBooksResponse approveReturn(ReturnBooksRequest request){
        Order order = orderService.getOrderEntity(request.orderNumber());

        Payment payment = paymentRepository.findByOrderNumber(request.orderNumber())
                .orElseThrow(NotFoundOrderNumberException::new);

        List<OrderedBook> returnBooks = findBooks(request.returnBooks());

        // 반품 사유 및 일자 체크
        validateReturnInfo(returnBooks, request.returnReason());

        // 반환 금액 계산 (현금/포인트)
        CancelAmountResult returnTotal = calculateAmounts(returnBooks);
        long returnCash = returnTotal.cancelCash();
        int returnPoint = returnTotal.cancelPoint();

        // 현재 배송정책 기준 회수비 계산
        LocalDateTime returnedAt = LocalDateTime.now();
        DeliveryPolicy policy = getPolicy(returnedAt);
        long returnFee = calculateReturnFee(request.returnReason(), policy);

        // 반품비 처리 (현금 -> 포인트 순서)
        CancelAmountResult amountResult = deductFee(returnCash, returnPoint, returnFee);
        returnCash = amountResult.cancelCash();
        returnPoint = amountResult.cancelPoint();

        boolean isMember = order.getMember() != null;
        if (isMember) {
            payment.applyCancel(returnCash, returnPoint);

            // (회원) 포인트 반환
            returnPoint += (int)returnCash;
            returnCash = 0;

            boolean isFullReturn = isAllReturn(order, returnBooks);

            if (isFullReturn) {
                // 전체 반품
                paymentHistoryRepository.save(
                        PaymentHistory.canceled(
                                payment,
                                returnPoint,
                                request.returnReason().name(),
                                returnedAt
                        )
                );
            } else {
                // 부분 반품
                paymentHistoryRepository.save(
                        PaymentHistory.partialCanceled(
                                payment,
                                returnPoint,
                                request.returnReason().name(),
                                returnedAt
                        )
                );
            }
        }
        else {
            // (비회원) 취소로 현금 환불
            PaymentCancelRequest pgRequest = new PaymentCancelRequest(
                    payment.getPaymentKey(),
                    request.orderNumber(),
                    request.returnReason().name(),
                    returnCash,     //현금 환불 금액
                    0,
                    request.returnBooks()
            );

            PaymentProvider provider = providerFactory.getProvider(payment.getPaymentType());
            provider.cancel(pgRequest);
        }

        orderService.applyOrderReturn(request.orderNumber(), returnBooks);

        ReturnBooksResponse response = new ReturnBooksResponse(
                request.orderNumber(),
                returnCash,
                returnPoint,
                returnFee,
                returnedAt,
                request.returnBooks()
        );

        eventPublisher.publishPaymentCanceled(new PaymentCancelResponse(request.orderNumber(), request.returnReason().name(), returnCash, returnPoint, returnedAt, request.returnBooks()));

        return response;
    }

    private List<OrderedBook> findBooks(List<CanceledBooksRequest> books) {
        return orderedBookRepository.findAllById(
                books.stream().map(CanceledBooksRequest::orderedBookId).toList()
        );
    }

    // 사유 입력
    private void updateReason(List<OrderedBook> books, String reason, LocalDateTime requestedAt) {
        books.forEach(book -> book.updateReason(reason, requestedAt));
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

    private CancelAmountResult deductFee(long cashAmount, int pointAmount, long fee) {

        // 1) 먼저 현금에서 차감
        long usedFromCash = Math.min(cashAmount, fee);
        long remainingCash = cashAmount - usedFromCash;

        // 2) 남은 금액이 있다면 포인트에서 차감
        long remainingFee = fee - usedFromCash;
        int remainingPoint = pointAmount;

        if (remainingFee > 0) {
            if (remainingFee > pointAmount) {
                throw new ExceedCancelAmountException();
            }
            remainingPoint -= (int) remainingFee;
        }

        return new CancelAmountResult(remainingCash, remainingPoint);
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
    private long calculateReturnFee(ReturnReasonType reason, DeliveryPolicy policy) {
        return reason.isCustomerFault() ? policy.getDeliveryFee() : 0;
    }

    // 남은 품목 확인
    private boolean isAllReturn(Order order, List<OrderedBook> returnBooks) {

        // 반품이 가능한 행(배송완료 상태)의 전체 수
        long returnableCount = order.getOrderedBooks().stream()
                .filter(ob -> ob.getUnitStatus() == OrderedBookStatusType.DELIVERED)
                .count();

        // 이번에 요청한 반품 행 중 실제로 반품 가능한 행만 카운트
        long requestCount = returnBooks.stream()
                .filter(ob -> ob.getUnitStatus() == OrderedBookStatusType.DELIVERED)
                .count();


        return returnableCount == requestCount;
    }

    // 출고일 기준 체크
    private void validateReturnInfo(List<OrderedBook> books, ReturnReasonType reason) {
        for (OrderedBook ob : books) {

            if (ob.getDeliveredAt() == null) {
                throw new RefundBeforeDeliveredException();
            }

            if (ob.getRequestAt() == null) {
                throw new InvalidRefundRequestTimeException();
            }

            long days = ChronoUnit.DAYS.between(
                    ob.getDeliveredAt().toLocalDate(),
                    ob.getRequestAt().toLocalDate()
            );

            if (reason.isCustomerFault()) {
                if (days > 10) {
                    throw new RefundPeriodExceededException();
                }
            } else {
                if (days > 30) {
                    throw new RefundPeriodExceededException();
                }
            }
        }
    }

}
