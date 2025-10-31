package shop.chaekmate.core.order.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import shop.chaekmate.core.order.entity.DeliveryPolicy;

public interface DeliveryPolicyRepository extends JpaRepository<DeliveryPolicy, Long> {

    Optional<DeliveryPolicy> findByDeletedAtIsNull();

    Page<DeliveryPolicy> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
