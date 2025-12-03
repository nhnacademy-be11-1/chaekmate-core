package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminMemberService {
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public List<MemberResponse> getActiveMembers() {
        return memberRepository.findAll().stream().map(MemberResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getDeletedMembers() {
        return memberRepository.findDeletedMembers().stream().map(MemberResponse::from).toList();
    }

    @Transactional
    public void deleteMember(Long memberId) {
        memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        memberRepository.deleteById(memberId);
    }

    @Transactional
    public void restoreMember(Long memberId) {
        int restored = memberRepository.restoreById(memberId);
        if (restored == 0) {
            throw new MemberNotFoundException();
        }

    }
}
