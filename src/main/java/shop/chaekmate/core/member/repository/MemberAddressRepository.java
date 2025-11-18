package shop.chaekmate.core.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.member.entity.MemberAddress;

import java.util.List;
import java.util.Optional;

public interface MemberAddressRepository extends JpaRepository<MemberAddress, Long> {
    int countByMemberId(Long memberId);
    List<MemberAddress> findAllByMemberId(Long memberId);
    Optional<MemberAddress> findByIdAndMemberId(Long addressId, Long memberId);
}
