package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.exception.DuplicatedLoginIdException;
import shop.chaekmate.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;
    @InjectMocks
    MemberService memberService;

//    @Test
//    void 회원가입_성공() {
//        var req = new CreateMemberRequest(
//                "1", "password", "user", "01012345678", "j@test.com", LocalDate.of(2003,5,1)
//        );
//
//        given(memberRepository.existsAnyByLoginId("1")).willReturn(false);
//        given(memberRepository.existsByEmail("j@test.com")).willReturn(false);
//
//        var saved = member("1", "hashed", "user", "01012345678", "j@test.com", LocalDate.of(2003,5,1));
//        given(memberRepository.save(any(Member.class))).willReturn(saved);
//
//        memberService.createMember(req);
//    }

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
