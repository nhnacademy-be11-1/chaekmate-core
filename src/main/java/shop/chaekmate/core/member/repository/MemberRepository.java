package shop.chaekmate.core.member.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.chaekmate.core.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    @Query(value = "select exists(select 1 from member where login_id = :loginId)", nativeQuery = true)
    int existsAnyByLoginId(@Param("loginId") String loginId);
    boolean existsByLoginId(@NotBlank(message = "ID는 필수입니다.") @Size(max = 20, message = "ID는 20자 이하로 입력해주세요.") String s);
    boolean existsByEmail(@NotBlank(message = "이메일은 필수입니다.") @Email(message = "이메일 형식이 올바르지 않습니다.") @Size(max = 50, message = "이메일은 50자 이하로 입력해주세요.") String email);
}
