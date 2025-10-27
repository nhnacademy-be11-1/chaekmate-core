package shop.chaekmate.core.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.coupon.entity.CouponAppliedBook;

public interface CouponAppliedBookRepository extends JpaRepository<CouponAppliedBook, Long> {
}
