package shop.chaekmate.core.payment.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.chaekmate.core.payment.entity.type.PaymentType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(
        name = "payment_history",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"payment_method", "payment_key"})
        }
)
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 21)
    private String orderNumber;

//    @Enumerated(STRING)
    @Column(name = "payment_type", nullable = false, length = 30)
    private String paymentType;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    private int pointUsed;

    @Column(nullable = false)
    private long totalAmount;

    @Enumerated(STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatusType paymentStatus;

    private OffsetDateTime approvedAt;

    private OffsetDateTime canceledAt;

    private OffsetDateTime failedAt;

    private String reason;

    // 성공
    public static PaymentHistory createApproved(String orderNumber, String paymentType, String paymentKey,
                                                long totalAmount, OffsetDateTime approvedAt) {

        PaymentHistory history = new PaymentHistory();
        history.orderNumber = orderNumber;
        history.paymentType = paymentType;
        history.paymentKey = paymentKey;
        history.totalAmount = totalAmount;
        history.paymentStatus = PaymentStatusType.APPROVED;
        history.approvedAt = approvedAt;
        return history;
    }

    // 실패
    public static PaymentHistory createFailed(String orderNumber, String paymentType,
                                              long totalAmount, OffsetDateTime failedAt, String reason) {

        PaymentHistory history = new PaymentHistory();
        history.orderNumber = orderNumber;
        history.paymentType = paymentType;
        history.totalAmount = totalAmount;
        history.paymentStatus = PaymentStatusType.FAILED;
        history.failedAt = failedAt;
        history.reason = reason;
        return history;
    }

    // 취소
    public static PaymentHistory createCanceled(String orderNumber, String paymentType, String paymentKey,
                                                long totalAmount, OffsetDateTime canceledAt, String reason) {

        PaymentHistory history = new PaymentHistory();
        history.orderNumber = orderNumber;
        history.paymentType = paymentType;
        history.paymentKey = paymentKey;
        history.totalAmount = totalAmount;
        history.paymentStatus = PaymentStatusType.CANCELED;
        history.canceledAt = canceledAt;
        history.reason = reason;
        return history;
    }
}
