package shop.chaekmate.core.point.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.entity.type.PointSpendType;

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

    //TODO: 후에 주문 exception 바꾸기
    @Transactional
    public CreatePointHistoryResponse earnPointForOrder(String orderNumber, long totalAmount) {

        log.info("[포인트 적립] 시작 - 주문번호: {}, totalAmount: {}", orderNumber, totalAmount);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalStateException("주문을 찾을 수 없습니다: " + orderNumber));

        Long memberId = order.getMember().getId();

        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException();
        }

        Grade grade = getCurrentGrade(memberId);
        int basePointRate = getOrderPolicyBaseRate();

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
    protected int getOrderPolicyBaseRate() {
        var policy = pointService.getPolicyByType(PointEarnedType.ORDER);
        return policy.point();
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
