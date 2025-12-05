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
import shop.chaekmate.core.payment.exception.AlreadyCanceledException;
import shop.chaekmate.core.payment.exception.ExceedCancelAmountException;

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

    @Column(length = 200)
    private String paymentKey;

    @Enumerated(STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatusType paymentStatus;

    @Column(nullable = false)
    private long totalAmount;

    @Column(nullable = false)
    private int pointUsed;

    @Column(nullable = false)
    private boolean deliveryFeeAdjusted = false;

    // 성공
    public static Payment createApproved(String orderNumber, String paymentKey, PaymentMethodType type,
                                         long totalAmount, int pointUsed) {
        Payment payment = new Payment();
        payment.orderNumber = orderNumber;
        payment.paymentKey = paymentKey;
        payment.paymentType = type;
        payment.totalAmount = totalAmount;
        payment.paymentStatus = PaymentStatusType.APPROVED;
        payment.pointUsed = pointUsed;
        payment.deliveryFeeAdjusted = false;
        return payment;
    }

    // 실패
    public static Payment createAborted(String orderNumber, String paymentKey, PaymentMethodType type,
                                        long totalAmount) {
        Payment payment = new Payment();
        payment.orderNumber = orderNumber;
        payment.paymentKey = paymentKey;
        payment.paymentType = type;
        payment.totalAmount = totalAmount;
        payment.paymentStatus = PaymentStatusType.ABORTED;
        payment.deliveryFeeAdjusted = false;
        return payment;
    }

    public void markDeliveryFeeAdjusted() {
        this.deliveryFeeAdjusted = true;
    }

    public void applyCancel(long cashCancelAmount, int pointCancelAmount) {

        if (this.paymentStatus == PaymentStatusType.CANCELED) {
            throw new AlreadyCanceledException();
        }

        // 1) 현금 차감
        if (cashCancelAmount > 0) {
            if (cashCancelAmount > this.totalAmount) {
                throw new ExceedCancelAmountException();
            }
            this.totalAmount -= cashCancelAmount;
        }

        // 2) 포인트 차감
        if (pointCancelAmount > 0) {
            if (pointCancelAmount > this.pointUsed) {
                throw new ExceedCancelAmountException();
            }
            this.pointUsed -= pointCancelAmount;
        }

        // 3) 상태 갱신
        this.paymentStatus =
                (this.totalAmount == 0 && this.pointUsed == 0)
                        ? PaymentStatusType.CANCELED
                        : PaymentStatusType.PARTIAL_CANCELED;
    }
}
