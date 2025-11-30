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
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {

    private final OrderService orderService;
    private final CouponClient couponClient;
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

    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentAborted(String orderNumber) {
        orderService.applyPaymentFail(orderNumber);
    }
    /*
    주문 내역 클릭
    주문 내역에는 하나의 주문번호 안에 여러 품목들이 있음
    그 중 개별 물품 취소 or 환불 클릭 사유 작성 버튼 클릭 시 -> 관리자 요청

    관리자 페이지 호출
    1. 주문한 책 상태 변경
    2. 주문한 책 수량 올리기
    3. 포인트 반환
     */

    // 결제 취소
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCanceled(PaymentCanceledEvent event) {
        PaymentCancelResponse res = event.cancelResponse();
        log.info("[EVENT] 결제 취소 수신 - 주문ID: {}", res.orderNumber());
    }
}
