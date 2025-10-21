package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
