package shop.chaekmate.core.point.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

public interface PointPolicyRepository extends JpaRepository<PointPolicy, Long> {
    java.util.Optional<PointPolicy> findByType(PointEarnedType type);

    boolean existsByType(PointEarnedType type);
}
