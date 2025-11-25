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

@Getter
@Table(name = "wrapper")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class Wrapper{

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
    
    public void updateWrapper(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
