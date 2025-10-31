package shop.chaekmate.core.point.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.point.entity.type.PointEarnedType;

import static jakarta.persistence.GenerationType.TABLE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "point_policy")
@Where(clause = "deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE point_policy SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class PointPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = TABLE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "point_earn_type", nullable = false, length = 30)
    private PointEarnedType type;

    @Column(nullable = false)
    private int point;

    public PointPolicy(PointEarnedType type, int point) {
        this.type = type;
        this.point = point;
    }

    public void updatePointPolicy(PointEarnedType type, Integer pointVal) {
        if (type != null) {
            this.type = type;
        }
        if (pointVal != null) {
            this.point = pointVal;
        }
    }

}
