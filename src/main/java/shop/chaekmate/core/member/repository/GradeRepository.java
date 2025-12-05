package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.member.entity.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByOrderByPointRate();
    Optional<Grade> findByName(String name);
    Optional<Grade> findByUpgradeStandardAmount(int upgradeStandardAmount);
    Boolean existsByUpgradeStandardAmount(int upgradeStandardAmount);
    Boolean existsByUpgradeStandardAmountAndIdNot(Long id, int upgradeStandardAmount);

    /**
     * 순수 주문금액에 맞는 등급 조회
     * upgradeStandardAmount가 순수금액 이하인 등급 중 가장 높은 등급
     */
    @Query("""
        SELECT g
        FROM Grade g
        WHERE g.upgradeStandardAmount <= :pureAmount
        ORDER BY g.upgradeStandardAmount DESC
        LIMIT 1
    """)
    Optional<Grade> findGradeByPureAmount(@Param("pureAmount") long pureAmount);

    /**
     * 모든 등급을 기준금액 오름차순으로 조회
     */
    @Query("""
        SELECT g
        FROM Grade g
        ORDER BY g.upgradeStandardAmount ASC
    """)
    List<Grade> findAllOrderByUpgradeStandardAmount();
}
