package shop.chaekmate.core.book.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table(name = "tag")
@SQLRestriction("deleted_at is null") // delete_at 이 null 인것만 조회 할 수 있게 하겠다
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE tag SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
public class Tag extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public Tag(String name) {
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
