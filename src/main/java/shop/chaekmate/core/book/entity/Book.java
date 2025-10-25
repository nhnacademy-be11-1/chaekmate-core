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

    @Column(columnDefinition = "TEXT")
    private String index;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 300, nullable = false)
    private String author;

    @Column(length = 300)
    private String publisher;

    private LocalDateTime publicationDatetime;

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
}
