package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.event.MemberRabbitEventPublisher;
import shop.chaekmate.core.member.dto.request.CreateMemberGradeHistoryRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.event.MemberEventPublisher;
import shop.chaekmate.core.member.exception.*;
import shop.chaekmate.core.member.repository.GradeRepository;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberEventPublisher eventPublisher;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;
    private final GradeRepository gradeRepository;
    private final MemberRabbitEventPublisher memberRabbitEventPublisher;

    @Transactional
    public void createMember(CreateMemberRequest request) {
        String password;
        PlatformType platformType;

        // PAYCO 회원가입 여부 확인 (loginId가 UUID 형식이면 PAYCO 회원가입)
        boolean isPaycoSignup = request.loginId().matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");

        if (isPaycoSignup) {
            // PAYCO 회원가입: password 값은 무시하고 랜덤 password 생성
            password = encoder.encode(generateRandomPassword());
            platformType = PlatformType.PAYCO;
        } else {
            // 일반 회원가입: password가 필수
            if (request.password() == null || request.password().isEmpty()) {
                throw new InvalidMemberRequestException();
            }
            password = encoder.encode(request.password());
            platformType = PlatformType.LOCAL;
        }

        Member member = new Member(
                request.loginId(),
                password,
                request.name(),
                request.phone(),
                request.email(),
                request.birthDate(),
                platformType
        );
        Member savedMember = memberRepository.save(member);
        Grade grade = gradeRepository.findByUpgradeStandardAmount(0).orElseThrow(GradeConfigurationException::new);
        MemberGradeHistory memberGradeHistory = new MemberGradeHistory(member, grade, "회원가입");
        memberGradeHistoryRepository.save(memberGradeHistory);

        MemberResponse memberResponse = new MemberResponse(
                savedMember.getId(),
                savedMember.getLoginId(),
                savedMember.getName(),
                savedMember.getPhone(),
                savedMember.getEmail(),
                savedMember.getBirthDate(),
                savedMember.getPlatformType(),
                savedMember.getLastLoginAt()
        );

        // 회원가입 이벤트 발행
        eventPublisher.publishMemberCreated(memberResponse);

        // 쿠폰 이벤트 발행
        memberRabbitEventPublisher.publishMemberSignedUp(savedMember.getId());
    }

    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 20; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public boolean isDuplicateLoginId(String loginId) {
        int bool = memberRepository.existsAnyByLoginId(loginId);
        return bool == 1;
    }

    public boolean isDuplicateEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Transactional
    public void updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 이메일이 변경된 경우에만 중복 체크
        if (!member.getEmail().equals(request.email())
                && memberRepository.existsByEmail(request.email())) {
            throw new DuplicatedEmailException();
        }

        member.update(
                request.name(),
                request.phone(),
                request.email()
        );
    }

    @Transactional(readOnly = true)
    public boolean isValidPassword(Long memberId, String password) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return encoder.matches(password, member.getPassword());
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return MemberResponse.from(member);
    }

    @Transactional(readOnly = true)
    public GradeResponse getMemberGrade(Long memberId) {
        MemberGradeHistory memberGradeHistory = memberGradeHistoryRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(MemberGradeHistoryNotFoundException::new);
        Long gradeId = memberGradeHistory.getGrade().getId();
        String name = memberGradeHistory.getGrade().getName();
        Byte pointRate = memberGradeHistory.getGrade().getPointRate();
        int upgradeStandardAmount = memberGradeHistory.getGrade().getUpgradeStandardAmount();

        return new GradeResponse(gradeId, name, pointRate, upgradeStandardAmount);
    }

    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAllByOrderByPointRate().stream().map(GradeResponse::from).toList();
    }

    @Transactional
    public void createMemberGradeHistory(CreateMemberGradeHistoryRequest request) {
        Member member = memberRepository.findById(request.memberId()).orElseThrow(MemberNotFoundException::new);
        Grade grade = gradeRepository.findByName(request.gradeName()).orElseThrow(GradeNotFoundException::new);
        String reason = request.reason();
        memberGradeHistoryRepository.save(new MemberGradeHistory(member, grade, reason));
    }
}
