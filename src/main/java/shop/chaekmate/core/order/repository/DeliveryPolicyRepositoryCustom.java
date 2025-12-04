package shop.chaekmate.core.order.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import shop.chaekmate.core.order.entity.DeliveryPolicy;

public interface DeliveryPolicyRepositoryCustom {
    Optional<DeliveryPolicy> findPolicyAt(LocalDateTime payTime);
}
