package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {
    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    MemberService memberService;

    @Test
    void 회원가입_성공() {
        var req = new CreateMemberRequest(
                "test", "password", "username", "01012345678", "j@test.com", LocalDate.of(2003,5,1)
        );

        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        memberService.createMember(req);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member saved = captor.getValue();
        assertThat(saved.getLoginId()).isEqualTo("test");
        assertThat(saved.getName()).isEqualTo("username");
        assertThat(saved.getPhone()).isEqualTo("01012345678");
        assertThat(saved.getEmail()).isEqualTo("j@test.com");
        assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(2003, 5, 1));
        assertThat(saved.getPlatformType()).isEqualTo(PlatformType.LOCAL);

        assertThat(saved.getPassword()).isNotEqualTo("password");
        var encoder = new BCryptPasswordEncoder();
        assertThat(encoder.matches("password", saved.getPassword())).isTrue();
    }

    @Test
    void 아이디_중복_조회_true() {
        given(memberRepository.existsAnyByLoginId("dup")).willReturn(1);

        boolean result = memberService.isDuplicateLoginId("dup");

        assertThat(result).isTrue();
    }

    @Test
    void 아이디_중복_조회_false() {
        given(memberRepository.existsAnyByLoginId("free")).willReturn(0);

        boolean result = memberService.isDuplicateLoginId("free");

        assertThat(result).isFalse();
    }

    @Test
    void 이메일_중복_조회_true() {
        given(memberRepository.existsByEmail("a@test.com")).willReturn(true);

        boolean result = memberService.isDuplicateEmail("a@test.com");

        assertThat(result).isTrue();
    }

    @Test
    void 이메일_중복_조회_false() {
        given(memberRepository.existsByEmail("b@test.com")).willReturn(false);

        boolean result = memberService.isDuplicateEmail("b@test.com");

        assertThat(result).isFalse();
    }
}
