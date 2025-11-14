package shop.chaekmate.core.member.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "member_address")
@NoArgsConstructor(access = PROTECTED)
@Entity
public class MemberAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(length = 100)
    private String memo;

    @Column(length = 200, nullable = false)
    private String streetName;

    @Column(length = 100, nullable = false)
    private String detail;

    @Column(length = 10, nullable = false)
    private int zipcode;

    public MemberAddress(Member member, String memo, String streetName, String detail, int zipcode) {
        this.member = member;
        this.memo = memo;
        this.streetName = streetName;
        this.detail = detail;
        this.zipcode = zipcode;
    }
}
