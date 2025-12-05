package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
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
import shop.chaekmate.core.member.dto.request.CreateMemberGradeHistoryRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.event.MemberEventPublisher;
import shop.chaekmate.core.member.exception.DuplicatedEmailException;
import shop.chaekmate.core.member.exception.MemberGradeHistoryNotFoundException;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
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
    void 회원가입_성공_일반회원() {
        // given
        var req = new CreateMemberRequest(
                "test", "password", "username", "01012345678", "j@test.com",
                LocalDate.of(2003, 5, 1)
        );

        Grade grade = spy(new Grade("브론즈", (byte) 1, 0));

        given(gradeRepository.findByUpgradeStandardAmount(0)).willReturn(Optional.of(grade));
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        memberService.createMember(req);

        // then
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
    void 회원가입_성공_PAYCO회원_UUID_loginId() {
        // given
        String uuidLoginId = "123e4567-e89b-12d3-a456-426614174000";
        var req = new CreateMemberRequest(
                uuidLoginId,
                "ignored-password",
                "paycoUser",
                "01098765432",
                "payco@test.com",
                LocalDate.of(1999, 1, 1)
        );

        Grade grade = spy(new Grade("브론즈", (byte) 1, 0));

        given(gradeRepository.findByUpgradeStandardAmount(0)).willReturn(Optional.of(grade));
        given(memberRepository.save(any(Member.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        // when
        memberService.createMember(req);

        // then
        ArgumentCaptor<Member> captor = ArgumentCaptor.forClass(Member.class);
        verify(memberRepository).save(captor.capture());

        Member saved = captor.getValue();

        assertAll(
                () -> assertThat(saved.getLoginId()).isEqualTo(uuidLoginId),
                () -> assertThat(saved.getPlatformType()).isEqualTo(PlatformType.PAYCO),
                () -> assertThat(saved.getPassword()).isNotBlank(),
                () -> assertThat(saved.getPassword()).doesNotContain("ignored-password")
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
    void 회원정보_수정_성공_이메일_변경없음() {
        // given
        Long memberId = 1L;
        Member member = new Member(
                "loginId",
                "encodedPw",
                "기존이름",
                "01011112222",
                "old@test.com",
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );

        UpdateMemberRequest request = new UpdateMemberRequest(
                "변경된이름",
                "01099998888",
                "old@test.com"
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        memberService.updateMember(memberId, request);

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo("변경된이름"),
                () -> assertThat(member.getPhone()).isEqualTo("01099998888"),
                () -> assertThat(member.getEmail()).isEqualTo("old@test.com")
        );
        // 이메일이 그대로라 existsByEmail는 호출되지 않아야 함
        then(memberRepository).should(never()).existsByEmail(anyString());
    }

    @Test
    void 회원정보_수정_성공_이메일_변경_중복아님() {
        // given
        Long memberId = 1L;
        Member member = new Member(
                "loginId",
                "encodedPw",
                "기존이름",
                "01011112222",
                "old@test.com",
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );

        UpdateMemberRequest request = new UpdateMemberRequest(
                "새이름",
                "01022223333",
                "new@test.com"
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByEmail("new@test.com")).willReturn(false);

        // when
        memberService.updateMember(memberId, request);

        // then
        assertAll(
                () -> assertThat(member.getName()).isEqualTo("새이름"),
                () -> assertThat(member.getPhone()).isEqualTo("01022223333"),
                () -> assertThat(member.getEmail()).isEqualTo("new@test.com")
        );
    }

    @Test
    void 회원정보_수정_실패_이메일_중복() {
        // given
        Long memberId = 1L;
        Member member = new Member(
                "loginId",
                "encodedPw",
                "기존이름",
                "01011112222",
                "old@test.com",
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );

        UpdateMemberRequest request = new UpdateMemberRequest(
                "새이름",
                "01022223333",
                "dup@test.com"
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByEmail("dup@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> memberService.updateMember(memberId, request))
                .isInstanceOf(DuplicatedEmailException.class);
    }

    @Test
    void 비밀번호_검증_성공() {
        // given
        Long memberId = 1L;
        String rawPassword = "Pw123456!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(rawPassword);

        Member member = new Member(
                "loginId",
                encoded,
                "이름",
                "01012341234",
                "pwd@test.com",
                LocalDate.of(1999, 1, 1),
                PlatformType.LOCAL
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        boolean result = memberService.isValidPassword(memberId, rawPassword);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 비밀번호_검증_실패_불일치() {
        // given
        Long memberId = 1L;
        String rawPassword = "Pw123456!";
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encoded = encoder.encode(rawPassword);

        Member member = new Member(
                "loginId",
                encoded,
                "이름",
                "01012341234",
                "pwd@test.com",
                LocalDate.of(1999, 1, 1),
                PlatformType.LOCAL
        );

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // when
        boolean result = memberService.isValidPassword(memberId, "WrongPassword!");

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 비밀번호_검증_실패_회원없음() {
        // given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.isValidPassword(memberId, "anyPw"))
                .isInstanceOf(MemberNotFoundException.class);
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

    @Test
    void 회원_등급_이력_생성_성공() {
        // given
        Long memberId = 1L;
        String gradeName = "골드";
        String reason = "테스트 등급 변경";

        Member member = new Member(
                "loginId",
                "encodedPw",
                "이름",
                "01012341234",
                "grade@test.com",
                LocalDate.of(1999, 1, 1),
                PlatformType.LOCAL
        );
        Grade grade = new Grade(gradeName);

        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(gradeRepository.findByName(gradeName)).willReturn(Optional.of(grade));

        var request = new CreateMemberGradeHistoryRequest(memberId, gradeName, reason);

        // when
        memberService.createMemberGradeHistory(request);

        // then
        verify(memberGradeHistoryRepository).save(any(MemberGradeHistory.class));
    }
}
