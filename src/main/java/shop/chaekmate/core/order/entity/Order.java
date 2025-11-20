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
@SQLDelete(sql = "UPDATE `order` SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(unique = true, length = 21, nullable = false)
    private String orderNumber;

    @Column(length = 50, nullable = false)
    private String ordererName;

    @Column(length = 20, nullable = false)
    private String ordererPhone;

    @Column(length = 200, nullable = false)
    private String ordererEmail;

    @Column(length = 50, nullable = false)
    private String recipientName;

    @Column(length = 20, nullable = false)
    private String recipientPhone;

    @Column(length = 5, nullable = false)
    private String zipcode;

    @Column(length = 200, nullable = false)
    private String streetName;

    @Column(length = 100, nullable = false)
    private String detail;

    @Column(length = 255)
    private String deliveryRequest;

    @Column(nullable = false)
    private LocalDate deliveryAt;

    @Column(nullable = false)
    private int deliveryFee;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderStatusType status;

    @Column(nullable = false)
    private long totalPrice;

    public static Order createOrderReady(
            Member member,
            String orderNumber,
            String orderName,
            String orderPhone,
            String orderEmail,
            String recipientName,
            String recipientPhone,
            String zipcode,
            String streetName,
            String detail,
            String deliveryRequest,
            LocalDate deliveryAt,
            int deliveryFee,
            long totalPrice
    ) {
        Order order = new Order();
        order.member = member;
        order.orderNumber = orderNumber;
        order.ordererName = orderName;
        order.ordererPhone = orderPhone;
        order.ordererEmail = orderEmail;
        order.recipientName = recipientName;
        order.recipientPhone = recipientPhone;
        order.zipcode = zipcode;
        order.streetName = streetName;
        order.detail = detail;
        order.deliveryRequest = deliveryRequest;
        order.deliveryAt = deliveryAt;
        order.deliveryFee = deliveryFee;
        order.totalPrice = totalPrice;

        order.status = OrderStatusType.PAYMENT_READY; // 결제 대기

        return order;
    }

    // 결제 실패
    public void markPaymentFailed() {
        this.status = OrderStatusType.PAYMENT_FAILED;
    }

    // 결제 성공 후 배송전 상태
    public void markPaymentSuccess() {
        this.status = OrderStatusType.WAITING;
    }

    // 배송 시작
    public void markShipping() {
        this.status = OrderStatusType.SHIPPING;
    }

    // 배송 완료
    public void markDelivered() {
        this.status = OrderStatusType.DELIVERED;
    }

    // 취소
    public void markCanceled() {
        this.status = OrderStatusType.CANCELED;
    }

    // 반품
    public void markReturned() {
        this.status = OrderStatusType.RETURNED;
    }
}
