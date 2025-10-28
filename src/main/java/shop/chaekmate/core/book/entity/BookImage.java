package shop.chaekmate.core.book.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Table(name = "book_image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE book SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class BookImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;

    public BookImage(Book book, String imageUrl) {
        this.book = book;
        this.imageUrl = imageUrl;
    }

    public void updateUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
