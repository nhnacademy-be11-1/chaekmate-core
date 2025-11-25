package shop.chaekmate.core.review.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.order.entity.OrderedBook;

@Getter
@Table(name = "review")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE review SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member memberId;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "ordered_book_id", nullable = false)
    private OrderedBook orderedBookId;

    @Column(columnDefinition = "comment", nullable = false)
    private String comment;

    @Column(nullable = false)
    private int rating;

    public static Review createReview(Member member, OrderedBook orderedBook, String comment, Integer rating) {
        Review review = new Review();
        review.memberId = member;
        review.orderedBookId = orderedBook;
        review.comment = comment;
        review.rating = rating;
        return review;
    }

    public void updateReview(String comment, Integer rating) {
        this.comment = comment;
        this.rating = rating;
    }
}
