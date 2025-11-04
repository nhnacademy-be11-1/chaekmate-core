package shop.chaekmate.core.payment.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import shop.chaekmate.core.payment.entity.type.PaymentMethod;
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

    @Enumerated(STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_key", length = 200)
    private String paymentKey;

    private long pointUsed;

    @Column(nullable = false)
    private long paymentPrice;

    @Enumerated(STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatusType paymentStatus;

    private LocalDateTime approvedAt;

    private LocalDateTime canceledAt;

    private LocalDateTime failedAt;

    private String reason;
}
