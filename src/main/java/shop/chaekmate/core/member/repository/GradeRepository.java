package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long> {
}
