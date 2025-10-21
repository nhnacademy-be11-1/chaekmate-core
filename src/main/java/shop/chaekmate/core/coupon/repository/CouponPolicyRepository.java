package shop.chaekmate.core.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.coupon.entity.CouponPolicy;

public interface CouponPolicyRepository extends JpaRepository<CouponPolicy, Long> {
}
