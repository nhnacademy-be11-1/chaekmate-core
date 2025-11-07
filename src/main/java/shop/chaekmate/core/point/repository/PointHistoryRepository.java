package shop.chaekmate.core.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointHistory;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

}
