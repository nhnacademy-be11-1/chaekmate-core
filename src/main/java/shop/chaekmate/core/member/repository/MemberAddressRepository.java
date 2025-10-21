package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.MemberAddress;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
}
