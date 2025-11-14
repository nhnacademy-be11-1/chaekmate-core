package shop.chaekmate.core.payment.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Enumerated(STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatusType paymentStatus;

    @Column(nullable = false)
    private long totalAmount;

    private String reason;

    @Column(nullable = false)
    private OffsetDateTime occurredAt;

    private static PaymentHistory create(Payment payment, PaymentStatusType status, long amount, String reason, OffsetDateTime occurredAt) {
        PaymentHistory history = new PaymentHistory();
        history.payment = payment;
        history.paymentStatus = status;
        history.totalAmount = amount;
        history.reason = reason;
        history.occurredAt = occurredAt;
        return history;
    }

    // 승인 이력
    public static PaymentHistory approved(Payment payment, long amount, OffsetDateTime occurredAt) {
        return create(payment, PaymentStatusType.APPROVED, amount, null, occurredAt);
    }

    // 실패 이력
    public static PaymentHistory aborted(Payment payment, long amount, String reason, OffsetDateTime occurredAt) {
        return create(payment, PaymentStatusType.ABORTED, amount, reason, occurredAt);
    }

    // 전체 취소 이력
    public static PaymentHistory canceled(Payment payment, long cancelAmount, String reason, OffsetDateTime canceledAt) {
        return create(payment, PaymentStatusType.CANCELED, cancelAmount, reason, canceledAt);
    }

    // 부분 취소 이력
    public static PaymentHistory partialCanceled(Payment payment, long cancelAmount, String reason, OffsetDateTime canceledAt) {
        return create(payment, PaymentStatusType.PARTIAL_CANCELED, cancelAmount, reason, canceledAt);
    }
}
