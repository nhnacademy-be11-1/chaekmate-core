package shop.chaekmate.core.point.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.point.entity.type.PointSpendType;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "point_history")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE point_history SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(STRING)
    @Column(name = "type", nullable = false, length = 10)
    private PointSpendType type;

    @Column(name = "amount", nullable = false)
    private int point;

    @Column(name = "source", nullable = false, length = 200)
    private String source;

    public PointHistory(Member member, PointSpendType type, int point, String source) {
        this.member = member;
        this.type = type;
        this.point = point;
        this.source = source;
    }
}
