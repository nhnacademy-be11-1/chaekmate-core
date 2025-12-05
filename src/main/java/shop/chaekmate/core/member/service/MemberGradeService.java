package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.repository.GradeRepository;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.dto.response.MemberPureAmountDto;
import shop.chaekmate.core.order.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberGradeService {

    private final MemberRepository memberRepository;
    private final GradeRepository gradeRepository;
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;
    private final OrderRepository orderRepository;

    /**
     * 모든 회원의 등급을 3개월 주문금액 기준으로 갱신
     */
    @Transactional
    public void updateAllMemberGrades() {
        log.info("회원 등급 자동 갱신 시작");

        // 1. 3개월 전 날짜 계산
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);

        // 2. 회원별 순수 주문금액 조회
        List<MemberPureAmountDto> pureAmounts = orderRepository.calculateMemberPureAmounts(threeMonthsAgo);
        log.info("등급 갱신 대상 회원 수: {}", pureAmounts.size());

        // 3. 회원별로 등급 갱신
        int updatedCount = 0;
        int unchangedCount = 0;

        for (MemberPureAmountDto dto : pureAmounts) {
            boolean updated = updateMemberGrade(dto.getMemberId(), dto.getPureAmount());
            if (updated) {
                updatedCount++;
            } else {
                unchangedCount++;
            }
        }

        // 4. 주문이 없는 회원들은 일반 등급으로 강등
        List<Long> memberIdsWithOrders = pureAmounts.stream()
                .map(MemberPureAmountDto::getMemberId)
                .collect(Collectors.toList());

        int downgraded = downgradeInactiveMembers(memberIdsWithOrders);

        log.info("회원 등급 자동 갱신 완료 - 변경: {}, 유지: {}, 강등: {}",
                updatedCount, unchangedCount, downgraded);
    }

    /**
     * 특정 회원의 등급 갱신
     *
     * @param memberId 회원 ID
     * @param pureAmount 순수 주문금액
     * @return 등급이 변경되었으면 true, 변경되지 않았으면 false
     */
    private boolean updateMemberGrade(Long memberId, Long pureAmount) {
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다: " + memberId));

        // 2. 현재 등급 조회
        Optional<MemberGradeHistory> currentGradeHistory =
                memberGradeHistoryRepository.findCurrentGradeByMemberId(memberId);

        // 3. 순수금액에 맞는 새 등급 조회
        Grade newGrade = gradeRepository.findGradeByPureAmount(pureAmount)
                .orElseGet(() -> getDefaultGrade()); // 조회 실패 시 기본 등급(일반)

        // 4. 등급 변경 여부 확인
        if (currentGradeHistory.isPresent()) {
            Grade currentGrade = currentGradeHistory.get().getGrade();

            // 등급이 동일하면 변경하지 않음
            if (currentGrade.getId().equals(newGrade.getId())) {
                log.debug("회원 {}의 등급 유지: {} (순수금액: {}원)",
                        memberId, currentGrade.getName(), pureAmount);
                return false;
            }

            // 등급 변경
            log.info("회원 {}의 등급 변경: {} -> {} (순수금액: {}원)",
                    memberId, currentGrade.getName(), newGrade.getName(), pureAmount);
        } else {
            // 첫 등급 부여
            log.info("회원 {}의 등급 최초 부여: {} (순수금액: {}원)",
                    memberId, newGrade.getName(), pureAmount);
        }

        // 5. 새 등급 이력 저장
        MemberGradeHistory newHistory = new MemberGradeHistory(
                member,
                newGrade,
                String.format("3개월 순수금액 %,d원 기준 자동 변경", pureAmount)
        );
        memberGradeHistoryRepository.save(newHistory);

        return true;
    }

    /**
     * 3개월 동안 주문이 없는 회원들을 일반 등급으로 강등
     *
     * @param memberIdsWithOrders 주문이 있는 회원 ID 목록
     * @return 강등된 회원 수
     */
    private int downgradeInactiveMembers(List<Long> memberIdsWithOrders) {
        // 1. 모든 회원 조회
        List<Member> allMembers = memberRepository.findAll();

        // 2. 주문이 없는 회원 필터링
        List<Member> inactiveMembers = allMembers.stream()
                .filter(member -> !memberIdsWithOrders.contains(member.getId()))
                .collect(Collectors.toList());

        if (inactiveMembers.isEmpty()) {
            return 0;
        }

        log.info("주문이 없는 회원 수: {}", inactiveMembers.size());

        // 3. 기본 등급(일반) 조회
        Grade defaultGrade = getDefaultGrade();

        // 4. 각 회원의 현재 등급 확인 후 필요시 강등
        int downgraded = 0;
        for (Member member : inactiveMembers) {
            Optional<MemberGradeHistory> currentHistory =
                    memberGradeHistoryRepository.findCurrentGradeByMemberId(member.getId());

            // 이미 일반 등급이거나 등급이 없으면 스킵
            if (currentHistory.isEmpty() ||
                    currentHistory.get().getGrade().getId().equals(defaultGrade.getId())) {
                continue;
            }

            // 일반 등급으로 강등
            MemberGradeHistory newHistory = new MemberGradeHistory(
                    member,
                    defaultGrade,
                    "3개월 주문 없음 - 일반 등급으로 강등"
            );
            memberGradeHistoryRepository.save(newHistory);

            log.info("회원 {}를 일반 등급으로 강등 (3개월 주문 없음)", member.getId());
            downgraded++;
        }

        return downgraded;
    }

    /**
     * 기본 등급(일반) 조회
     * 없으면 가장 낮은 등급 반환
     */
    private Grade getDefaultGrade() {
        return gradeRepository.findByName("일반")
                .or(() -> gradeRepository.findAllOrderByUpgradeStandardAmount()
                        .stream()
                        .findFirst())
                .orElseThrow(() -> new IllegalStateException("등급 정보가 존재하지 않습니다"));
    }
}
