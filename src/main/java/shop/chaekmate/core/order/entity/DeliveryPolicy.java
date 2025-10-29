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
import shop.chaekmate.core.common.entity.BaseEntity;

@Getter
@Table(name = "delivery_policy")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE delivery_policy SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class DeliveryPolicy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int freeStandardAmount;

    @Column(nullable = false)
    private int deliveryFee;

    public DeliveryPolicy(int freeStandardAmount, int deliveryFee) {
        this.freeStandardAmount = freeStandardAmount;
        this.deliveryFee = deliveryFee;
    }

    public boolean equalsDeliveryPolicy(int freeStandardAmount, int deliveryFee){
        return this.freeStandardAmount == freeStandardAmount && this.deliveryFee == deliveryFee;
    }
}
