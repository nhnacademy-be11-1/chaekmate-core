package shop.chaekmate.core.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.coupon.entity.CouponAppliedCategory;

public interface CouponAppliedCategoryRepository extends JpaRepository<CouponAppliedCategory, Long> {
}
