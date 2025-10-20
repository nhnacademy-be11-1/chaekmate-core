package shop.chaekmate.core.member.entity;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import shop.chaekmate.core.common.entity.BaseEntity;
import shop.chaekmate.core.member.entity.type.GradeType;

@Getter
@Table(name = "grade")
@SQLRestriction("deleted_at is null")
@NoArgsConstructor(access = PROTECTED)
@SQLDelete(sql = "UPDATE grade SET deleted_at = CURRENT_TIMESTAMP WHERE id = ?")
@Entity
public class Grade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = STRING)
    @Column(length = 20, nullable = false)
    private GradeType gradeType;

    @Column(nullable = false)
    private int pointRate;

    @Column(nullable = false)
    private int upgradeStandardAmount; // 승급 기준 금액
}
