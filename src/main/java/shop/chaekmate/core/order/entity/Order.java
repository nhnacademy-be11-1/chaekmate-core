package shop.chaekmate.core.order.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.order.entity.type.OrderStatusType;

@Getter
@Table(name = "`order`")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE order SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(unique = true, length = 21, nullable = false)
    private String number;

    @Column(length = 20, nullable = false)
    private String phone;

    @Column(length = 200, nullable = false)
    private String address;

    @Column(nullable = false)
    private LocalDate deliveryDate;

    @Column(nullable = false)
    private int deliveryFee;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderStatusType status;

    @Column(nullable = false)
    private long totalPrice;
}
