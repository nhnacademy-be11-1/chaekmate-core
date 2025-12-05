package shop.chaekmate.core.order.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.member.service.MemberGradeService;

/**
 * 회원 등급 자동 변경 스케줄러
 *
 * 매일 새벽 2시에 실행되어 3개월 순수 주문금액 기준으로 회원 등급을 갱신합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberGradeScheduler {

    private final MemberGradeService memberGradeService;

    /**
     * 회원 등급 자동 갱신
     *
     * Cron 표현식: 0 0 2 * * *
     * - 초: 0
     * - 분: 0
     * - 시: 2 (새벽 2시)
     * - 일: * (매일)
     * - 월: * (매월)
     * - 요일: * (모든 요일)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void updateMemberGrades() {
        log.info("=== 회원 등급 자동 갱신 스케줄러 시작 ===");

        try {
            memberGradeService.updateAllMemberGrades();
            log.info("=== 회원 등급 자동 갱신 스케줄러 완료 ===");
        } catch (Exception e) {
            log.error("회원 등급 자동 갱신 중 오류 발생", e);
        }
    }

    /**
     * 테스트용: 수동 실행 메서드
     * 필요시 컨트롤러에서 호출하여 즉시 실행 가능
     */
    public void updateMemberGradesManually() {
        log.info("=== 회원 등급 수동 갱신 시작 ===");
        memberGradeService.updateAllMemberGrades();
        log.info("=== 회원 등급 수동 갱신 완료 ===");
    }
}
