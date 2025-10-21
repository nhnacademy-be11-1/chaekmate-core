package shop.chaekmate.core.cart.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.member.entity.Member;

@Getter
@Table(name = "cart")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE cart SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Cart extends BaseEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
