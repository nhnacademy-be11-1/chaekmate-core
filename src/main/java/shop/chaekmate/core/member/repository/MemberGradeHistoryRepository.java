package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.member.entity.MemberGradeHistory;

import java.util.Optional;

public interface MemberGradeHistoryRepository extends JpaRepository<MemberGradeHistory, Long> {
    Optional<MemberGradeHistory> findByMemberId(Long memberId);

    Optional<MemberGradeHistory> findFirstByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<MemberGradeHistory> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);

    /**
     * 회원의 현재 등급 조회 (가장 최근 등급 이력)
     */
    @Query("""
        SELECT mgh
        FROM MemberGradeHistory mgh
        WHERE mgh.member.id = :memberId
        ORDER BY mgh.createdAt DESC
        LIMIT 1
    """)
    Optional<MemberGradeHistory> findCurrentGradeByMemberId(@Param("memberId") Long memberId);
}
