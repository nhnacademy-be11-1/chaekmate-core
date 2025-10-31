package shop.chaekmate.core.point.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "point_history")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE tag SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class PointHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private long point;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(nullable = false, length = 200)
    private String source;

}
