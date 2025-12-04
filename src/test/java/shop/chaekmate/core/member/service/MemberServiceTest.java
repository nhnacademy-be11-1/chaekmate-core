package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.event.MemberRabbitEventPublisher;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.event.MemberEventPublisher;
import shop.chaekmate.core.member.exception.MemberGradeHistoryNotFoundException;
import shop.chaekmate.core.member.repository.GradeRepository;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberGradeHistoryRepository memberGradeHistoryRepository;

    @Mock
    GradeRepository gradeRepository;

    @Mock
    MemberEventPublisher memberEventPublisher;

    @Mock
    MemberRabbitEventPublisher memberRabbitEventPublisher;

    @InjectMocks
    MemberService memberService;

    @Test
    void 회원가입_성공() {
        var req = new CreateMemberRequest(
                "test", "password", "username", "01012345678", "j@test.com",
                LocalDate.of(2003, 5, 1)
        );

        Grade grade = spy(new Grade("브론즈", (byte) 1, 0));

        given(gradeRepository.findByUpgradeStandardAmount(0)).willReturn(Optional.of(grade));

        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        memberService.createMember(req);

        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member saved = captor.getValue();
        var encoder = new BCryptPasswordEncoder();

        assertAll(
                () -> assertThat(saved.getLoginId()).isEqualTo("test"),
                () -> assertThat(saved.getName()).isEqualTo("username"),
                () -> assertThat(saved.getPhone()).isEqualTo("01012345678"),
                () -> assertThat(saved.getEmail()).isEqualTo("j@test.com"),
                () -> assertThat(saved.getBirthDate()).isEqualTo(LocalDate.of(2003, 5, 1)),
                () -> assertThat(saved.getPlatformType()).isEqualTo(PlatformType.LOCAL),
                () -> assertThat(saved.getPassword()).isNotEqualTo("password"),
                () -> assertThat(encoder.matches("password", saved.getPassword())).isTrue()
        );
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

    @Test
    void 회원_등급_조회_성공() {
        Long memberId = 1L;
        Grade grade = new Grade("일반");

        MemberGradeHistory history = mock(MemberGradeHistory.class);
        given(history.getGrade()).willReturn(grade);
        given(memberGradeHistoryRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                .willReturn(Optional.of(history));

        GradeResponse result = memberService.getMemberGrade(memberId);

        assertThat(result.name()).isEqualTo("일반");
        assertThat(result.pointRate()).isEqualTo(grade.getPointRate());
        assertThat(result.upgradeStandardAmount()).isEqualTo(grade.getUpgradeStandardAmount());
    }

    @Test
    void 회원_등급_조회_실패_히스토리_없음() {
        Long memberId = 1L;
        given(memberGradeHistoryRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberGrade(memberId))
                .isInstanceOf(MemberGradeHistoryNotFoundException.class);
    }

    @Test
    void 전체_등급_목록_조회_성공() {
        Grade general = new Grade("일반");
        Grade royal = new Grade("로얄");

        given(gradeRepository.findAllByOrderByPointRate())
                .willReturn(List.of(general, royal));

        List<GradeResponse> result = memberService.getAllGrades();

        assertThat(result).hasSize(2);
        assertThat(result)
                .extracting(GradeResponse::name)
                .containsExactly("일반", "로얄");
        assertThat(result)
                .extracting(GradeResponse::pointRate)
                .containsExactly(general.getPointRate(), royal.getPointRate());
    }
}
