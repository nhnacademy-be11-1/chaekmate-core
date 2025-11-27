package shop.chaekmate.core.point.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.event.MemberCreatedEvent;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.event.PaymentApprovedEvent;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.entity.type.PointSpendType;
import shop.chaekmate.core.point.service.PointEarnService;
import shop.chaekmate.core.point.service.PointHistoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointEarnService pointEarnService;
    private final PointHistoryService pointHistoryService;
    private final OrderRepository orderRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse response = event.approveResponse();
        log.info("[포인트 이벤트] 결제 승인 이벤트 수신 - 주문번호: {}, 금액: {}, 사용포인트: {}",
                response.orderNumber(), response.totalAmount(), response.pointUsed());

        try {
            Order order = orderRepository.findByOrderNumber(response.orderNumber())
                    .orElseThrow(NotFoundOrderNumberException::new);

            if(order.getMember()==null){
                log.warn("[포인트 이벤트] 비회원 주문이므로 포인트 차감하지 않음 - 주문번호: {}",
                        response.orderNumber());
                return;
            }

            long memberId = order.getMember().getId();

            // 포인트 사용(차감) 처리
            if (response.pointUsed() > 0) {
                CreatePointHistoryRequest spendRequest = new CreatePointHistoryRequest(
                        null,
                        memberId,
                        PointSpendType.SPEND,
                        response.pointUsed(),
                        String.format("주문 결제 - 주문번호: %s", response.orderNumber())
                );

                pointHistoryService.spendPointHistory(memberId, spendRequest);
                log.info("[포인트 이벤트] 포인트 차감 완료 - 주문번호: {}, 차감포인트: {}",
                        response.orderNumber(), response.pointUsed());
            }

            // 포인트 적립 처리
            pointEarnService.earnPointForOrder(
                    response.orderNumber(),
                    response.totalAmount()
            );

            log.info("[포인트 이벤트] 포인트 적립 완료 - 주문번호: {}", response.orderNumber());

        } catch (Exception e) {
            // 포인트 적립 실패 시 로그만 남기고 예외를 던지지 않음
            // - 결제는 이미 완료되었으므로 포인트 적립 실패가 결제를 롤백하면 안 됨
            log.error("[포인트 이벤트] 포인트 적립 실패 - 주문번호: {}, 오류: {}",
                    response.orderNumber(), e.getMessage(), e);
        }
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMemberCreated(MemberCreatedEvent event) {
        MemberResponse memberResponse = event.memberResponse();
        log.info("[포인트 이벤트] 회원가입 이벤트 수신 - 회원ID: {}, 로그인ID: {}",
                memberResponse.id(), memberResponse.loginId());

        try {
            // 회원가입 환영 포인트 적립 처리
            CreatePointHistoryResponse response = pointEarnService.earnPointForWelcome(memberResponse.id());

            if (response == null) {
                log.warn("[포인트 이벤트] 회원가입 환영 포인트 적립 결과 null - 회원ID: {} (포인트 정책 미설정 또는 포인트 0)",
                        memberResponse.id());
            } else {
                log.info("[포인트 이벤트] 회원가입 환영 포인트 적립 완료 - 회원ID: {}, 포인트: {}",
                        memberResponse.id(), response.point());
            }

        } catch (Exception e) {
            // 포인트 적립 실패 시 로그만 남기고 예외를 던지지 않음
            // - 회원가입은 이미 완료되었으므로 포인트 적립 실패가 회원가입을 롤백하면 안 됨
            log.error("[포인트 이벤트] 회원가입 환영 포인트 적립 실패 - 회원ID: {}, 오류: {}",
                    memberResponse.id(), e.getMessage(), e);
        }
    }
}

