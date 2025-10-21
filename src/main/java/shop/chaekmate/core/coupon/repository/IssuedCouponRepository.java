package shop.chaekmate.core.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.coupon.entity.IssuedCoupon;

public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
}
