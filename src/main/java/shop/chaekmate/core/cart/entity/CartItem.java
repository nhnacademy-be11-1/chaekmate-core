package shop.chaekmate.core.cart.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@Entity
@Getter
@Table(name = "cart_item")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE cart_item SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private int quantity;

    // 장바구니에 도서를 처음 담는 시점에 생성됨
    // 장바구니 아이템의 최소 수량 규칙: 항상 1부터 시작
    public static CartItem create(Cart cart, Book book) {
        return new CartItem(cart, book, 1);
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    private CartItem(Cart cart, Book book, int quantity) {
        this.cart = cart;
        this.book = book;
        this.quantity = quantity;
    }
}
