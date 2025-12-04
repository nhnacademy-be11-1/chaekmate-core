package shop.chaekmate.core.payment.event;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.order.service.OrderService;
import shop.chaekmate.core.payment.client.CouponClient;
import shop.chaekmate.core.payment.client.DoorayMessageType;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.service.DoorayService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;
    private final CouponClient couponClient;
    private final DoorayService doorayService;
    // 결제 성공
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse res = event.approveResponse();

        Order order = orderService.getOrderEntity(res.orderNumber());

        // 주문 내 쿠폰 목록 추출 (null 제외, 중복 제거)
        List<Long> couponIds = order.getOrderedBooks().stream()
                .map(OrderedBook::getIssuedCouponId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        // 쿠폰 일괄 사용 처리
        if (!couponIds.isEmpty()) {
            couponClient.useCouponsBulk(order.getMember().getId(), couponIds);
            log.info("[EVENT] 쿠폰 {}개 일괄 사용 처리 완료", couponIds.size());
        }

        // 두레이 알림
        doorayService.sendMessage(
                res.orderNumber(),
                List.of(DoorayMessageType.PAYMENT_SUCCESS)
        );

    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentAborted(String orderNumber) {
        doorayService.sendMessage(
                orderNumber,
                List.of(DoorayMessageType.PAYMENT_FAILED)
        );
    }

    // 결제 취소
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCanceled(PaymentCanceledEvent event) {
        PaymentCancelResponse res = event.cancelResponse();

        // 반품인지 구분
        boolean isReturn = res.canceledCash() == 0 &&
                (res.cancelReason().equals("CHANGE_OF_MIND") ||
                        res.cancelReason().equals("ORDER_MISTAKE") ||
                        res.cancelReason().equals("DELIVERY_FAILURE") ||
                        res.cancelReason().equals("DAMAGED_GOODS") ||
                        res.cancelReason().equals("WRONG_DELIVERY"));

        if (isReturn) {
            doorayService.sendMessage(
                    res.orderNumber(),
                    List.of(DoorayMessageType.RETURN_COMPLETED)
            );

        } else {
            doorayService.sendMessage(
                    res.orderNumber(),
                    List.of(DoorayMessageType.PAYMENT_CANCELED)
            );
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReturnRequested(ReturnRequestedEvent event) {
        String orderNumber = event.response().orderNumber();
        doorayService.sendMessage(
                orderNumber,
                List.of(DoorayMessageType.RETURN_REQUESTED)
        );
    }
}
