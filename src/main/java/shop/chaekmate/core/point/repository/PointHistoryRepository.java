package shop.chaekmate.core.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointPolicy;

public interface PointHistoryRepository extends JpaRepository<PointPolicy, Long> {

}