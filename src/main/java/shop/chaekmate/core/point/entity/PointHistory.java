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

    @Column(name = "amount", nullable = false)
    private Long point;

    @Enumerated(STRING)
    @Column(name = "type", nullable = false, length = 10)
    private PointSpendType type;

    @Column(name = "source", nullable = false, length = 200)
    private String source;

    public PointHistory(Member member, Long point, PointSpendType type, String source) {
        this.member = member;
        this.point = point;
        this.type = type;
        this.source = source;
    }
}
