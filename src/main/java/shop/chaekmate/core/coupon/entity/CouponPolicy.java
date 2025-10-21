package shop.chaekmate.core.coupon.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.coupon.entity.type.CouponAppliedPeriodType;
import shop.chaekmate.core.coupon.entity.type.CouponType;
import shop.chaekmate.core.coupon.entity.type.DiscountType;

@Getter
@Table(name = "coupon_policy")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE coupon_policy SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class CouponPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Enumerated(value = STRING)
    @Column(length = 50, nullable = false)
    private CouponType type;

    private Long couponAppliedTargetId;

    @Enumerated(value = STRING)
    @Column(length = 50, nullable = false)
    private CouponAppliedPeriodType appliedPeriodType;

    private LocalDateTime appliedStartedAt;

    private LocalDateTime appliedExpiredAt;

    @Enumerated(value = STRING)
    @Column(length = 10, nullable = false)
    private DiscountType discountType;

    @Column(nullable = false)
    private int discountValue;

    private Integer minAvailableAmount;

    private Long maxAppliedAmount;

    @Column(nullable = false)
    private long remainingQuantity;
}
