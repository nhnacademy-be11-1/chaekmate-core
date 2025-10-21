package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
