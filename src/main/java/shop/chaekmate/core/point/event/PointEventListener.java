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
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.event.PaymentAbortedEvent;
import shop.chaekmate.core.payment.event.PaymentApprovedEvent;
import shop.chaekmate.core.payment.event.PaymentCanceledEvent;
import shop.chaekmate.core.review.dto.response.CreateReviewResponse;
import shop.chaekmate.core.review.event.ReviewCreatedEvent;
import shop.chaekmate.core.review.event.TextReviewCreatedEvent;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.entity.type.PointSpendType;
import shop.chaekmate.core.point.repository.PointHistoryRepository;
import shop.chaekmate.core.point.service.PointEarnService;
import shop.chaekmate.core.point.service.PointHistoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class PointEventListener {

    private final PointEarnService pointEarnService;
    private final PointHistoryService pointHistoryService;
    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentApproved(PaymentApprovedEvent event) {
        PaymentApproveResponse response = event.approveResponse();
        log.info("[포인트 이벤트] 결제 승인 이벤트 수신 - 주문번호: {}, 금액: {}, 사용포인트: {}",
                response.orderNumber(), response.totalAmount(), response.pointUsed());

        try {
            // 포인트 사용(차감) 처리
            if (response.totalAmount() > 0) {
                Order order = orderRepository.findByOrderNumber(response.orderNumber())
                        .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다: " + response.orderNumber()));

                // 비회원 주문인 경우 포인트 차감하지 않음
                Long memberId = order.getMember().getId();

                if (memberId != null) {

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

                    // 포인트 적립 처리
                    pointEarnService.earnPointForOrder(
                            response.orderNumber(),
                            response.totalAmount()
                    );

                    log.info("[포인트 이벤트] 포인트 적립 완료 - 주문번호: {}", response.orderNumber());
                } else {
                    log.warn("[포인트 이벤트] 비회원 주문이므로 포인트 차감하지 않음 - 주문번호: {}",
                            response.orderNumber());
                }
            }
        } catch (Exception e) {
            // 포인트 적립 실패 시 로그만 남기고 예외를 던지지 않음
            // - 결제는 이미 완료되었으므로 포인트 적립 실패가 결제를 롤백하면 안 됨
            log.error("[포인트 이벤트] 포인트 적립 실패 - 주문번호: {}, 오류: {}",
                    response.orderNumber(), e.getMessage(), e);
        }
    }
    // 결제 실패 시 포인트 처리하지 않음 (명시적으로 로그만 남김)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentAborted(PaymentAbortedEvent event) {
        log.info("[포인트 이벤트] 결제 실패 이벤트 수신 - 주문번호: {}, 에러코드: {}, 메시지: {}",
                event.orderNumber(), event.abortedResponse().code(), event.abortedResponse().message());
        log.info("[포인트 이벤트] 결제 실패로 인해 포인트 차감 및 적립이 발생하지 않음 - 주문번호: {}",
                event.orderNumber());
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

    // 결제 취소 시 포인트 역처리
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentCanceled(PaymentCanceledEvent event) {
        PaymentCancelResponse response = event.cancelResponse();
        log.info("[포인트 이벤트] 결제 취소 이벤트 수신 - 주문번호: {}, 취소현금: {}, 취소포인트{}",
                response.orderNumber(), response.canceledCash(), response.canceledPoint());

        try {
            Order order = orderRepository.findByOrderNumber(response.orderNumber())
                    .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다: " + response.orderNumber()));

            // 비회원 주문인 경우 포인트 처리하지 않음
            if (order.getMember() == null) {
                log.warn("[포인트 이벤트] 비회원 주문이므로 포인트 취소 처리하지 않음 - 주문번호: {}",
                        response.orderNumber());
                return;
            }

            Long memberId = order.getMember().getId();

            String spendSource = String.format("주문 결제 - 주문번호: %s", response.orderNumber());
            String earnSource = String.format("주문 완료 - 주문번호: %s", response.orderNumber());

            // 반품인지 취소인지 구분
            // 반품: canceledCash = 0이고 cancelReason이 반품 사유(ReturnReasonType)인 경우
            // 취소: 그 외의 경우
            boolean isReturn = response.canceledCash() == 0 &&
                    (response.cancelReason() != null &&
                            (response.cancelReason().equals("CHANGE_OF_MIND") ||
                                    response.cancelReason().equals("ORDER_MISTAKE") ||
                                    response.cancelReason().equals("DELIVERY_FAILURE") ||
                                    response.cancelReason().equals("DAMAGED_GOODS") ||
                                    response.cancelReason().equals("WRONG_DELIVERY")));

            if (isReturn) {
                // 반품의 경우: canceledPoint에 이미 모든 금액(기존 포인트 + 현금)이 포함되어 있음
                // 따라서 canceledPoint를 그대로 적립하면 됨
                int returnPoint = response.canceledPoint();

                if (returnPoint > 0) {
                    log.info("[포인트 이벤트] 반품 시 포인트 환불 - 주문번호: {}, 포인트: {}",
                            response.orderNumber(), returnPoint);

                    CreatePointHistoryRequest returnEarnRequest = new CreatePointHistoryRequest(
                            null,
                            memberId,
                            PointSpendType.EARN,
                            returnPoint,
                            String.format("주문 반품 환불 - 주문번호: %s", response.orderNumber())
                    );

                    pointHistoryService.earnPointHistory(memberId, returnEarnRequest);
                    log.info("[포인트 이벤트] 반품 시 포인트 환불 완료 - 주문번호: {}, 포인트: {}",
                            response.orderNumber(), returnPoint);
                }
            } else {
                // 취소의 경우: 기존 포인트 사용/적립 이력을 역처리
                // 1. 주문 결제 시 사용한 포인트 찾아서 적립으로 역처리 (사용했던 포인트를 다시 돌려줌)
                pointHistoryRepository.findByMemberIdAndSource(memberId, spendSource)
                        .ifPresent(spendHistory -> {
                            int canceledSpendPoint = spendHistory.getPoint();
                            log.info("[포인트 이벤트] 취소 적립금액 처리 - 주문번호: {}, 포인트: {}",
                                    response.orderNumber(), canceledSpendPoint);

                            CreatePointHistoryRequest earnRequest = new CreatePointHistoryRequest(
                                    null,
                                    memberId,
                                    PointSpendType.EARN,
                                    canceledSpendPoint,
                                    String.format("주문결제사용취소 - 주문번호: %s", response.orderNumber())
                            );

                            pointHistoryService.earnPointHistory(memberId, earnRequest);
                            log.info("[포인트 이벤트] 취소 적립금액 처리 완료 - 주문번호: {}, 포인트: {}",
                                    response.orderNumber(), canceledSpendPoint);
                        });

                // 2. 주문 완료 시 적립한 포인트 찾아서 사용으로 역처리 (적립했던 포인트를 다시 차감)
                pointHistoryRepository.findByMemberIdAndSource(memberId, earnSource)
                        .ifPresent(earnHistory -> {
                            int canceledEarnPoint = earnHistory.getPoint();
                            log.info("[포인트 이벤트] 취소 사용금액 처리 - 주문번호: {}, 포인트: {}",
                                    response.orderNumber(), canceledEarnPoint);

                            CreatePointHistoryRequest spendRequest = new CreatePointHistoryRequest(
                                    null,
                                    memberId,
                                    PointSpendType.SPEND,
                                    canceledEarnPoint,
                                    String.format("주문결제적립취소 - 주문번호: %s", response.orderNumber())
                            );

                            pointHistoryService.spendPointHistory(memberId, spendRequest);
                            log.info("[포인트 이벤트] 취소 사용금액 처리 완료 - 주문번호: {}, 포인트: {}",
                                    response.orderNumber(), canceledEarnPoint);
                        });
            }

        } catch (Exception e) {
            // 포인트 취소 실패 시 로그만 남기고 예외를 던지지 않음
            // - 결제 취소는 이미 완료되었으므로 포인트 취소 실패가 결제 취소를 롤백하면 안 됨
            log.error("[포인트 이벤트] 포인트 취소 실패 - 주문번호: {}, 오류: {}",
                    response.orderNumber(), e.getMessage(), e);
        }
    }

    // 리뷰 작성 시 포인트 적립 (이미지와 텍스트 둘 다 있을 때)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleReviewCreated(ReviewCreatedEvent event) {
        log.info("[포인트 이벤트] 이미지 리뷰 작성 이벤트 수신 - 리뷰ID: {}, 회원ID: {}",
                event.reviewId(), event.memberId());

        try {
            // 중복 적립 방지: 이미 해당 리뷰에 대해 포인트를 적립했는지 확인
            String reviewSource = String.format("이미지 리뷰 작성 - 리뷰ID: %s", event.reviewId());
            boolean alreadyEarned = pointHistoryRepository.findByMemberIdAndSource(event.memberId(), reviewSource)
                    .isPresent();

            if (alreadyEarned) {
                log.warn("[포인트 이벤트] 이미 해당 리뷰에 대해 포인트를 적립했음 - 리뷰ID: {}, 회원ID: {}",
                        event.reviewId(), event.memberId());
                return;
            }

            CreatePointHistoryResponse response = pointEarnService.earnPointForReview(event.memberId(), event.reviewId());

            if (response == null) {
                log.warn("[포인트 이벤트] 이미지 리뷰 작성 포인트 적립 결과 null - 리뷰ID: {}, 회원ID: {} (포인트 정책 미설정 또는 포인트 0)",
                        event.reviewId(), event.memberId());
            } else {
                log.info("[포인트 이벤트] 이미지 리뷰 작성 포인트 적립 완료 - 리뷰ID: {}, 회원ID: {}, 포인트: {}",
                        event.reviewId(), event.memberId(), response.point());
            }

        } catch (Exception e) {
            log.error("[포인트 이벤트] 이미지 리뷰 작성 포인트 적립 실패 - 리뷰ID: {}, 회원ID: {}, 오류: {}",
                    event.reviewId(), event.memberId(), e.getMessage(), e);
        }
    }

    // 텍스트 리뷰 작성 시 포인트 적립
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTextReviewCreated(TextReviewCreatedEvent event) {
        CreateReviewResponse response = event.createReviewResponse();
        log.info("[포인트 이벤트] 텍스트 리뷰 작성 이벤트 수신 - 리뷰ID: {}, 회원ID: {}",
                response.id(), response.memberId());

        try {
            // 중복 적립 방지: 이미 해당 리뷰에 대해 포인트를 적립했는지 확인
            String reviewSource = String.format("텍스트 리뷰 작성 - 리뷰ID: %s", response.id());
            boolean alreadyEarned = pointHistoryRepository.findByMemberIdAndSource(response.memberId(), reviewSource)
                    .isPresent();

            if (alreadyEarned) {
                log.warn("[포인트 이벤트] 이미 해당 리뷰에 대해 포인트를 적립했음 - 리뷰ID: {}, 회원ID: {}",
                        response.id(), response.memberId());
                return;
            }

            CreatePointHistoryResponse pointResponse = pointEarnService.earnPointForTextReview(response.memberId(), response.id());

            if (pointResponse == null) {
                log.warn("[포인트 이벤트] 텍스트 리뷰 작성 포인트 적립 결과 null - 리뷰ID: {}, 회원ID: {} (포인트 정책 미설정 또는 포인트 0)",
                        response.id(), response.memberId());
            } else {
                log.info("[포인트 이벤트] 텍스트 리뷰 작성 포인트 적립 완료 - 리뷰ID: {}, 회원ID: {}, 포인트: {}",
                        response.id(), response.memberId(), pointResponse.point());
            }

        } catch (Exception e) {
            log.error("[포인트 이벤트] 텍스트 리뷰 작성 포인트 적립 실패 - 리뷰ID: {}, 회원ID: {}, 오류: {}",
                    response.id(), response.memberId(), e.getMessage(), e);
        }
    }
}

