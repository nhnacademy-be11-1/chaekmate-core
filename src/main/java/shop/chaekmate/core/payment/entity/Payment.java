package shop.chaekmate.core.payment.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"payment_type", "payment_key"})
        }
)
@Getter
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "order_number", unique = true, nullable = false, length = 21)
    private String orderNumber;

    @Enumerated(STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    private PaymentMethodType paymentType;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    @Enumerated(STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatusType paymentStatus;

    @Column(nullable = false)
    private long totalAmount;

    private Integer pointUsed;

    public static Payment createApproved(String orderNumber, String paymentKey, PaymentMethodType type,
                                         long totalAmount, Integer pointUsed) {
        Payment payment = new Payment();
        payment.orderNumber = orderNumber;
        payment.paymentKey = paymentKey;
        payment.paymentType = type;
        payment.totalAmount = totalAmount;
        payment.paymentStatus = PaymentStatusType.APPROVED;
        payment.pointUsed = (pointUsed == null) ? 0 : pointUsed;
        return payment;
    }

    public static Payment createAborted(String orderNumber, String paymentKey, PaymentMethodType type,
                                        long totalAmount) {
        Payment payment = new Payment();
        payment.orderNumber = orderNumber;
        payment.paymentKey = paymentKey;
        payment.paymentType = type;
        payment.totalAmount = totalAmount;
        payment.paymentStatus = PaymentStatusType.ABORTED;
        return payment;
    }

    public void cancel() {
        this.paymentStatus = PaymentStatusType.CANCELED;
    }
}
