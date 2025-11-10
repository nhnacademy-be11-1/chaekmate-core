package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberGradeResponse;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberAddress;
import shop.chaekmate.core.member.entity.MemberGradeHistory;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.exception.*;
import shop.chaekmate.core.member.repository.MemberAddressRepository;
import shop.chaekmate.core.member.repository.MemberGradeHistoryRepository;
import shop.chaekmate.core.member.repository.MemberRepository;



@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final MemberGradeHistoryRepository  memberGradeHistoryRepository;
    private final MemberAddressRepository memberAddressRepository;

    private static final int MAX_ADDRESS_COUNT = 10;

    @Transactional
    public void createMember(CreateMemberRequest request) {
        validateMember(request.loginId(), request.email());
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

    private void validateMember(String loginId, String email) {
        int bool = memberRepository.existsAnyByLoginId(loginId);
        if (bool == 1) {
            throw new DuplicatedLoginIdException();
        }
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicatedEmailException();
        }
    }

    public boolean isDuplicateLoginId(String loginId) {
        int bool = memberRepository.existsAnyByLoginId(loginId);
        return bool == 1;
    }

    @Transactional
    public void deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        memberRepository.delete(member);
    }

    @Transactional(readOnly = true)
    public MemberGradeResponse getMemberGrade(Long memberId) {
        MemberGradeHistory memberGradeHistory = memberGradeHistoryRepository.findByMemberId(memberId)
                .orElseThrow(MemberHistoryNotFoundException::new);

        String name = memberGradeHistory.getGrade().getName();
        Byte pointRate = memberGradeHistory.getGrade().getPointRate();

        return new MemberGradeResponse(name,pointRate);

    }

    @Transactional
    public void createAddress(CreateAddressRequest request, Long memberId) {
        validateAddressCount(memberId);
        MemberAddress memberAddress = new MemberAddress(
                memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new),
                request.memo(),
                request.streetName(),
                request.detail(),
                request.zipcode()
        );
        memberAddressRepository.save(memberAddress);
    }

    private void validateAddressCount(Long memberId) {
        int count = memberAddressRepository.countByMemberId(memberId);
        if (count >= MAX_ADDRESS_COUNT) {
            throw new AddressLimitExceededException();
        }
    }

    @Transactional
    public void deleteAddress(Long id) {
        memberAddressRepository.findById(id).orElseThrow(AddressNotFoundException::new);

    }
}
