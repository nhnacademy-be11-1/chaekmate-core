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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;

@Getter
@Table(name = "ordered_book")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE ordered_book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class OrderedBook extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int originalPrice;

    private Integer salesPrice;

    private Integer discountPrice;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wrapper_id")
    private Wrapper wrapper;

    private Integer wrapperPrice;

    private Long issuedCouponId;

    private Integer couponDiscount;

    private Integer pointUsed;

    @Column(nullable = false)
    private int finalUnitPrice;

    @Column(nullable = false)
    private long totalPrice;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderedBookStatusType unitStatus;

    @Column(length = 255)
    private String reason;

    public static OrderedBook createOrderDetailReady(
            Order order,
            Book book,
            int quantity,
            int originalPrice,
            Integer salesPrice,
            Integer discountPrice,
            Wrapper wrapper,
            Integer wrapperPrice,
            Long issuedCouponId,
            Integer couponDiscount,
            Integer pointUsed,
            int finalUnitPrice,
            long totalPrice
    ) {
        OrderedBook ob = new OrderedBook();
        ob.order = order;
        ob.book = book;
        ob.quantity = quantity;

        ob.originalPrice = originalPrice;
        ob.salesPrice = salesPrice;
        ob.discountPrice = discountPrice;

        ob.wrapper = wrapper;
        ob.wrapperPrice = wrapperPrice;

        ob.issuedCouponId = issuedCouponId;
        ob.couponDiscount = couponDiscount;
        ob.pointUsed = pointUsed;

        ob.finalUnitPrice = finalUnitPrice;
        ob.totalPrice = totalPrice;

        ob.unitStatus = OrderedBookStatusType.PAYMENT_READY; // 결제전 기본 상태

        return ob;
    }

    // 결제 실패
    public void markPaymentFailed() {
        this.unitStatus = OrderedBookStatusType.PAYMENT_FAILED;
    }

    // 결제 완료
    public void markPaymentCompleted() {
        this.unitStatus = OrderedBookStatusType.PAYMENT_COMPLETE;
    }

    // 배송 시작
    public void markShipping() {
        this.unitStatus = OrderedBookStatusType.SHIPPING;
    }

    // 배송 완료
    public void markDelivered() {
        this.unitStatus = OrderedBookStatusType.DELIVERED;
    }

    // 취소 완료
    public void markCanceled() {
        this.unitStatus = OrderedBookStatusType.CANCELED;
    }

    // 반품 요청
    public void markReturnRequest() {
        this.unitStatus = OrderedBookStatusType.RETURN_REQUEST;
    }

    // 반품 완료
    public void markReturned() {
        this.unitStatus = OrderedBookStatusType.RETURNED;
    }

    public void updateReason(String reason) {
        this.reason = reason;
    }
}
