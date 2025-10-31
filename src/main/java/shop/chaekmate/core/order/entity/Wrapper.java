package shop.chaekmate.core.order.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;

@Getter
@Table(name = "wrapper")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE wrapper SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Wrapper extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    public Wrapper(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    //이름 판단 로직
    public boolean equalsName(String name){
        return this.name.equals(name);
    }

    public void updateWrapper(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
