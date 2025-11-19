package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.MemberGradeHistory;

import java.util.Optional;

public interface MemberGradeHistoryRepository extends JpaRepository<MemberGradeHistory, Long> {
    Optional<MemberGradeHistory> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}
