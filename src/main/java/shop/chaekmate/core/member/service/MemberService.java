package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.dto.response.MemberResponse;
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
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public GradeResponse getMemberGrade(Long memberId) {
        MemberGradeHistory memberGradeHistory = memberGradeHistoryRepository.findTopByMemberIdOrderByCreatedAtDesc(memberId)
                .orElseThrow(MemberGradeHistoryNotFoundException::new);
        String name = memberGradeHistory.getGrade().getName();
        Byte pointRate = memberGradeHistory.getGrade().getPointRate();
        int upgradeStandardAmount = memberGradeHistory.getGrade().getUpgradeStandardAmount();

        return new GradeResponse(name, pointRate, upgradeStandardAmount);
    }

    public List<GradeResponse> getAllGrades() {
        return gradeRepository.findAll().stream().map(GradeResponse::from).toList();
    }
}
