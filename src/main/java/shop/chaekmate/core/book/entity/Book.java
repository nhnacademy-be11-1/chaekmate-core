package shop.chaekmate.core.book.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.exception.InsufficientStockException;
import shop.chaekmate.core.common.entity.BaseEntity;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "book")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Book extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 250, nullable = false)
    private String title;

    @Column(name = "`index`", columnDefinition = "TEXT")
    private String index;

    @Column(name = "`description`", columnDefinition = "TEXT")
    private String description;

    @Column(length = 300, nullable = false)
    private String author;

    @Column(length = 300)
    private String publisher;

    private LocalDateTime publishedAt;

    @Column(length = 13, nullable = false)
    private String isbn;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int salesPrice;

    @Column(nullable = false)
    private boolean isWrappable;

    @Column(nullable = false)
    private long views;

    @Column(nullable = false)
    private boolean isSaleEnd;

    @Column(nullable = false)
    private int stock;

    @Builder
    public Book(String title, String index, String description, String author,
                String publisher, LocalDateTime publishedAt, String isbn, int price,
                int salesPrice, boolean isWrappable, long views,
                boolean isSaleEnd, int stock) {
        this.title = title;
        this.index = index;
        this.description = description;
        this.author = author;
        this.publisher = publisher;
        this.publishedAt = publishedAt;
        this.isbn = isbn;
        this.price = price;
        this.salesPrice = salesPrice;
        this.isWrappable = isWrappable;
        this.views = views;
        this.isSaleEnd = isSaleEnd;
        this.stock = stock;
    }

    public void update(BookUpdateRequest request) {
        this.title = request.title();
        this.index = request.index();
        this.description = request.description();
        this.author = request.author();
        this.publisher = request.publisher();
        this.publishedAt = request.publishedAt();
        this.isbn = request.isbn();
        this.price = request.price();
        this.salesPrice = request.salesPrice();
        this.isWrappable = request.isWrappable();
        this.isSaleEnd = request.isSaleEnd();
        this.stock = request.stock();
    }

    // 재고 체크
    public boolean hasStock(int quantity) {
        return this.stock >= quantity;
    }

    // 재고 처리
    public void decreaseStock(int qty) {
        if (this.stock < qty) {
            throw new InsufficientStockException();
        }
        this.stock -= qty;
    }

    // 재고 복구 (취소 시)
    public void increaseStock(int qty) {
        this.stock += qty;
    }
}
