package shop.chaekmate.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.order.entity.DeliveryPolicy;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {
}
