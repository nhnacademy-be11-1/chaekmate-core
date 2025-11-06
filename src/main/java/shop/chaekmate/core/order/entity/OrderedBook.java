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
import shop.chaekmate.core.order.entity.type.OrderStatusType;

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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "wrapper_id")
    private Wrapper wrapper;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private int bookPrice;

    private Long issuedCouponId;

    private int couponDiscount;

    private int pointUsed;

    @Column(nullable = false)
    private int finalUnitPrice;

    @Column(nullable = false)
    private long totalPrice;

    @Enumerated(value = STRING)
    @Column(nullable = false)
    private OrderStatusType unitStatus;
}
