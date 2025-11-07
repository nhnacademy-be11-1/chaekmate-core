package shop.chaekmate.core.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointHistory;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findByMemberId(Long memberId);
}