package shop.chaekmate.core.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PointPolicyType;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.entity.type.PointSpendType;
import shop.chaekmate.core.point.repository.PointPolicyRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointEarnService {
    private final PointHistoryService pointHistoryService;
    private final PointService pointService;
    private final MemberRepository memberRepository;
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public CreatePointHistoryResponse earnPointForWelcome(Long memberId) {
        log.info("[포인트 적립] 회원가입 환영 포인트 적립 시작 - 회원ID: {}", memberId);

        if (!memberRepository.existsById(memberId)) {
            log.warn("[포인트 적립] 회원을 찾을 수 없음 - 회원ID: {}", memberId);
            throw new MemberNotFoundException();
        }

        try {
            var policy = pointService.getPolicyByType(PointEarnedType.WELCOME);
            int welcomePoint = policy.point();

            if (welcomePoint <= 0) {
                log.info("[포인트 적립] 회원가입 환영 포인트가 0이므로 적립하지 않음 - 회원ID: {}", memberId);
                return null;
            }

            CreatePointHistoryRequest request = new CreatePointHistoryRequest(
                    null,
                    memberId,
                    PointSpendType.EARN,
                    welcomePoint,
                    "회원가입"
            );

            CreatePointHistoryResponse response = pointHistoryService.earnPointHistory(memberId, request);
            log.info("[포인트 적립] 회원가입 환영 포인트 적립 완료 - 회원ID: {}, 포인트: {}", memberId, welcomePoint);
            return response;
        } catch (shop.chaekmate.core.point.exception.PointPolicyNotFoundException e) {
            log.warn("[포인트 적립] 회원가입 환영 포인트 정책이 설정되지 않아 포인트를 적립하지 않음 - 회원ID: {}", memberId);
            // 포인트 정책이 없으면 적립하지 않고 null 반환 (예외를 던지지 않음)
            return null;
        } catch (Exception e) {
            log.error("[포인트 적립] 회원가입 환영 포인트 적립 실패 - 회원ID: {}, 오류: {}", memberId, e.getMessage(), e);
            throw e;
        }
    }

    //TODO: 후에 주문 exception 바꾸기
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreatePointHistoryResponse earnPointForOrder(String orderNumber, long totalAmount) {

        log.info("[포인트 적립] 시작 - 주문번호: {}, totalAmount: {}", orderNumber, totalAmount);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다: " + orderNumber));

        Long memberId = order.getMember().getId();

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        Grade grade = getCurrentGrade(memberId);
        int basePointRate = getPolicyBaseRate(PointEarnedType.ORDER);

        int earnedPoint = calculateBaseRatePoint(totalAmount, basePointRate) + calculateGradeRatePoint(totalAmount, grade.getPointRate());

        if (earnedPoint <= 0) {
            return null;
        }

        CreatePointHistoryRequest request = new CreatePointHistoryRequest(
                null,
                memberId,
                PointSpendType.EARN,
                earnedPoint,
                String.format("주문 완료 - 주문번호: %s", orderNumber)
        );

        return pointHistoryService.earnPointHistory(memberId, request);
    }

    @Transactional (readOnly = true)
    public Grade getCurrentGrade(Long memberId) {
        Optional<MemberGradeHistory> latedstGradeHistory =
                memberGradeHistoryRepository.findFirstByMemberIdOrderByCreatedAtDesc(memberId);

        if(latedstGradeHistory.isPresent()) {
            return latedstGradeHistory.get().getGrade();
        }

        throw new IllegalStateException("회원의 등급을 찾을 수 없습니다." + memberId);
    }

    //타입 주고 고치기
    @Transactional(readOnly = true)
    protected int getPolicyBaseRate(PointEarnedType pointEarnedType) {
        return pointService.getPolicyByType(pointEarnedType).point();
    }

    private int calculateBaseRatePoint(long amount, int baseRate) {

        double pointRateDecimal = baseRate / 100.0;
        double calculatedPoint = amount * pointRateDecimal;

        return (int) calculatedPoint;
    }

    private int calculateGradeRatePoint(long amount, Byte pointRate) {

        double pointRateDecimal = pointRate / 100.0;
        double calculatedPoint = amount * pointRateDecimal;

        return (int) calculatedPoint;
    }
}
