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
import shop.chaekmate.core.payment.exception.InvalidCancelAmountException;

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

    // 성공
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

    // 실패
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

//    // 전체 취소
//    public long cancel() {
//        if (this.paymentStatus == PaymentStatusType.CANCELED) {
//            throw new AlreadyCanceledException();
//        }
//
//        long canceledAmount = this.totalAmount;
//        this.totalAmount = 0L;
//        this.paymentStatus = PaymentStatusType.CANCELED;
//
//        return canceledAmount;
//    }
//
//    // 부분 취소
//    public void partialCancel(Long cancelAmount) {
//        if (this.paymentStatus == PaymentStatusType.CANCELED) {
//            throw new AlreadyCanceledException();
//        }
//        if (cancelAmount == null || cancelAmount <= 0) {
//            throw new InvalidCancelAmountException();
//        }
//        if (cancelAmount > this.totalAmount) {
//            throw new ExceedCancelAmountException();
//        }
//
//        this.totalAmount -= cancelAmount;
//
//        this.paymentStatus = (this.totalAmount == 0) ? PaymentStatusType.CANCELED : PaymentStatusType.PARTIAL_CANCELED;
//    }

    public void cancelOrPartial(Long cancelAmount) {
        if (this.paymentStatus == PaymentStatusType.CANCELED) {
            throw new AlreadyCanceledException();
        }

        final long cash = this.totalAmount;
        final int point = (this.pointUsed == null ? 0 : this.pointUsed);
        final long totalPaid = cash + point;

        // 전체 취소
        if (cancelAmount == null || cancelAmount == totalPaid) {
            this.totalAmount = 0L;
            this.pointUsed = 0;
            this.paymentStatus = PaymentStatusType.CANCELED;
            return;
        }

        if (cancelAmount <= 0) {
            throw new InvalidCancelAmountException();
        }
        if (cancelAmount > totalPaid) {
            throw new ExceedCancelAmountException();
        }

        // 부분취소: 현금 먼저 차감 → 남으면 포인트 차감
        long remaining = cancelAmount;

        if (remaining >= cash) {
            // 현금 전부 차감 후 포인트 일부 차감
            remaining -= cash;
            this.totalAmount = 0L;
            this.pointUsed = (int) Math.max(point - remaining, 0);
        } else {
            // 현금 일부만 차감
            this.totalAmount = cash - remaining;
        }

        this.paymentStatus = (this.totalAmount == 0 && this.pointUsed == 0)
                        ? PaymentStatusType.CANCELED
                        : PaymentStatusType.PARTIAL_CANCELED;
    }

}
