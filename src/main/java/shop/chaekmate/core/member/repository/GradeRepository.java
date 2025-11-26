package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.Grade;

import java.util.List;
import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findAllByOrderByPointRate();
    Optional<Grade> findByName(String name);
    Optional<Grade> findByUpgradeStandardAmount(int upgradeStandardAmount);
    Boolean existsByUpgradeStandardAmount(int upgradeStandardAmount);
    Boolean existsByUpgradeStandardAmountAndIdNot(Long id, int upgradeStandardAmount);
}
