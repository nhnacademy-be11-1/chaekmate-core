package shop.chaekmate.core.book.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;

import lombok.AllArgsConstructor;

@Getter
@Table(name = "book")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
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

    public Book(String title, String index, String description, String author, String publisher,
                LocalDateTime publishedAt, String isbn, int price, int salesPrice, boolean isWrappable,
                long views, boolean isSaleEnd, int stock) {
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
}
