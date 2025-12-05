package shop.chaekmate.core.member.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminMemberServiceTest {

    @InjectMocks
    AdminMemberService adminMemberService;

    @Mock
    MemberRepository memberRepository;

    @Test
    void 활성_회원_목록_조회_성공() {
        // given
        Member m1 = new Member(
                "user1",
                "pw1",
                "홍길동",
                "010-1111-2222",
                "user1@test.com",
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );
        Member m2 = new Member(
                "user2",
                "pw2",
                "김철수",
                "010-3333-4444",
                "user2@test.com",
                LocalDate.of(1999, 5, 10),
                PlatformType.LOCAL
        );

        given(memberRepository.findAll()).willReturn(List.of(m1, m2));

        // when
        List<MemberResponse> result = adminMemberService.getActiveMembers();

        // then
        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).loginId());
        assertEquals("user2", result.get(1).loginId());

        then(memberRepository).should().findAll();
    }

    @Test
    void 탈퇴_회원_목록_조회_성공() {
        // given
        Member deleted = new Member(
                "deleted1",
                "pw",
                "이탈퇴",
                "010-5555-6666",
                "deleted@test.com",
                LocalDate.of(1995, 3, 15),
                PlatformType.LOCAL
        );

        given(memberRepository.findDeletedMembers()).willReturn(List.of(deleted));

        // when
        List<MemberResponse> result = adminMemberService.getDeletedMembers();

        // then
        assertEquals(1, result.size());
        assertEquals("deleted1", result.get(0).loginId());
        assertEquals("이탈퇴", result.get(0).name());

        then(memberRepository).should().findDeletedMembers();
    }

    @Test
    void 회원_삭제_성공() {
        // given
        Long memberId = 1L;
        Member m = new Member(
                "user1",
                "pw1",
                "홍길동",
                "010-1111-2222",
                "user1@test.com",
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(m));

        // when
        adminMemberService.deleteMember(memberId);

        // then
        then(memberRepository).should().findById(memberId);
        then(memberRepository).should().deleteById(memberId);
    }

    @Test
    void 회원_삭제시_존재하지_않으면_예외() {
        // given
        Long memberId = 999L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MemberNotFoundException.class,
                () -> adminMemberService.deleteMember(memberId));

        then(memberRepository).should().findById(memberId);
        then(memberRepository).should(never()).deleteById(anyLong());
    }

    @Test
    void 회원_복구_성공() {
        // given
        Long memberId = 1L;
        given(memberRepository.restoreById(memberId)).willReturn(1);

        // when (예외가 안 나면 성공)
        adminMemberService.restoreMember(memberId);

        // then
        then(memberRepository).should().restoreById(memberId);
    }

    @Test
    void 회원_복구시_업데이트_건수가_0이면_예외() {
        // given
        Long memberId = 999L;
        given(memberRepository.restoreById(memberId)).willReturn(0);

        // when & then
        assertThrows(MemberNotFoundException.class,
                () -> adminMemberService.restoreMember(memberId));

        then(memberRepository).should().restoreById(memberId);
    }
}
