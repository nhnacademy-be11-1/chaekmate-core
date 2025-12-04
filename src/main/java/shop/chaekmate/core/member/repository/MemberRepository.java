package shop.chaekmate.core.member.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.member.entity.Member;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "select count(*) from member where login_id = :loginId", nativeQuery = true)
    int existsAnyByLoginId(@Param("loginId") String loginId);
    boolean existsByEmail(@NotBlank(message = "이메일은 필수입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.") String email);
    // 탈퇴 회원 목록 조회
    @Query(value = "select * from member where deleted_at is not null", nativeQuery = true)
    List<Member> findDeletedMembers();
    // 탈퇴 회원 포함 단일 조회
    @Query(value = "select * from member where id = :id", nativeQuery = true)
    Optional<Member> findByIdIncludingDeleted(@Param("id") Long id);
    // 탈퇴 회원 복구
    @Modifying
    @Query(value = "update member set deleted_at = null where id = :id", nativeQuery = true)
    int restoreById(@Param("id") Long id);
}
