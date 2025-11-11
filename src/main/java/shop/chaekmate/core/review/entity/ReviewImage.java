package shop.chaekmate.core.review.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "review_iamge")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE review_image SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long Id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review_id;

    @Lob
    @Column(name = "image_url", nullable = false, columnDefinition = "TEXT")
    private String imageUrl;
}
