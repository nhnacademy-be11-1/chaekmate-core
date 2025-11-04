package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.exception.DuplicatedEmailException;
import shop.chaekmate.core.member.exception.DuplicatedLoginIdException;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new DuplicatedLoginIdException();
        }
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicatedEmailException();
        }

        Member member = new Member(
                request.loginId(),
                encoder.encode(request.password()),
                request.name(),
                request.phone(),
                request.email(),
                request.birthDate(),
                PlatformType.LOCAL
        );

        Member saved = memberRepository.save(member);
        return toResponse(saved);
    }

    public MemberResponse readMember(Long id) {
        return toResponse(findMember(id));
    }

    public List<MemberResponse> readAllMembers() {
        return memberRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        Member member = findMember(id);

        if (!member.getEmail().equals(request.email()) && memberRepository.existsByEmail(request.email())) {
            throw new DuplicatedEmailException();
        }

        member.modifyMember(request.name(), request.email(), request.phone());
        return toResponse(member);
    }


    @Transactional
    public void deleteMember(Long id) {
        Member m = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        m.markAsDeleted(); // deletedAt = now()
    }


    private Member findMember(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
    }

    private MemberResponse toResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getLoginId(),
                member.getName(),
                member.getPhone(),
                member.getEmail(),
                member.getBirthDate(),
                member.getPlatformType(),
                member.getLastLoginAt()
        );
    }
}
