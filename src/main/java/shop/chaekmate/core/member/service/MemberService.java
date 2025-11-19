package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateMemberGradeHistoryRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PlatformType;
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
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final MemberGradeHistoryRepository memberGradeHistoryRepository;
    private final GradeRepository gradeRepository;

    @Transactional
    public void createMember(CreateMemberRequest request) {
        Member member = new Member(
                request.loginId(),
                encoder.encode(request.password()),
                request.name(),
                request.phone(),
                request.email(),
                request.birthDate(),
                PlatformType.LOCAL
        );
        memberRepository.save(member);
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
    public MemberResponse getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        return new MemberResponse(member.getLoginId(), member.getName(), member.getPhone(), member.getEmail(), member.getBirthDate());
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
