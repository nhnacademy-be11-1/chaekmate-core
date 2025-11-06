package shop.chaekmate.core.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

import java.util.Optional;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {

    boolean existsByType(PointEarnedType type);

    Optional<PointPolicy> findByType(PointEarnedType type);
}
