package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.exception.DuplicatedEmailException;
import shop.chaekmate.core.member.exception.DuplicatedLoginIdException;
import shop.chaekmate.core.member.exception.InvalidMemberRequestException;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    MemberService memberService;

    @Test
    void 회원가입_성공() {
        var req = new CreateMemberRequest(
                "1", "password", "user", "01012345678", "j@test.com", LocalDate.of(2003,5,1)
        );

        given(memberRepository.existsAnyByLoginId("1")).willReturn(false);
        given(memberRepository.existsByEmail("j@test.com")).willReturn(false);

        var saved = member("1", "hashed", "user", "01012345678", "j@test.com", LocalDate.of(2003,5,1));
        given(memberRepository.save(any(Member.class))).willReturn(saved);

        MemberResponse res = memberService.createMember(req);

        assertThat(res.loginId()).isEqualTo("1");
        assertThat(res.email()).isEqualTo("j@test.com");
        then(memberRepository).should(times(1)).save(any(Member.class));
    }

    @Test
    void 중복_아이디_회원가입() {
        var req = new CreateMemberRequest(
                "dupId", "pw", "이름", "010", "a@test.com", LocalDate.of(2000,1,1)
        );
        given(memberRepository.existsAnyByLoginId("dupId")).willReturn(true);

        assertThatThrownBy(() -> memberService.createMember(req))
                .isInstanceOf(DuplicatedLoginIdException.class);
    }

    @Test
    void 회원조회_성공() {
        var m = member("user1", "pw", "홍길동", "010", "u@test.com", LocalDate.of(2000,1,1));
        given(memberRepository.findById(1L)).willReturn(Optional.of(m));

        var res = memberService.readMember(1L);

        assertThat(res.loginId()).isEqualTo("user1");
    }

    @Test
    void 회원조회_실패() {
        given(memberRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.readMember(999L))
                .isInstanceOf(MemberNotFoundException.class);
    }

    @Test
    void 회원수정_성공() {
        var m = member("user1", "pw", "홍길동", "01011112222", "old@test.com", LocalDate.of(2000,1,1));
        given(memberRepository.findById(10L)).willReturn(Optional.of(m));
        given(memberRepository.existsByEmail("new@test.com")).willReturn(false);

        var req = new UpdateMemberRequest("임꺽정", "01022223333", "new@test.com");

        var res = memberService.updateMember(10L, req);

        assertThat(res.name()).isEqualTo("임꺽정");
        assertThat(res.email()).isEqualTo("new@test.com");
    }

    @Test
    void 회원수정_실패_이메일_중복() {
        var m = member("user1", "pw", "홍길동", "010", "old@test.com", LocalDate.of(2000,1,1));
        given(memberRepository.findById(10L)).willReturn(Optional.of(m));
        given(memberRepository.existsByEmail("dup@test.com")).willReturn(true);

        var req = new UpdateMemberRequest("임꺽정", "010", "dup@test.com");

        // expect
        assertThatThrownBy(() -> memberService.updateMember(10L, req))
                .isInstanceOf(DuplicatedEmailException.class)
                .hasMessageContaining("이메일");
    }

    @Test
    void 회원삭제_성공() {
        var m = member("user1", "pw", "홍길동", "010", "u@test.com", LocalDate.of(2000,1,1));
        given(memberRepository.findById(7L)).willReturn(Optional.of(m));

        memberService.deleteMember(7L);

        verify(memberRepository).delete(m);          // 여기까지만 본다
        verify(memberRepository).findById(7L);
    }

    private Member member(String loginId, String password, String name, String phone, String email, LocalDate birth) {
        return new Member(loginId, password, name, phone, email, birth, PlatformType.LOCAL);
    }
}
