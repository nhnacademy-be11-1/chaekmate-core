package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.response.AddressResponse;
import shop.chaekmate.core.member.entity.MemberAddress;
import shop.chaekmate.core.member.exception.AddressLimitExceededException;
import shop.chaekmate.core.member.exception.AddressNotFoundException;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberAddressRepository;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AddressService {
    private final MemberAddressRepository memberAddressRepository;
    private final MemberRepository memberRepository;

    private static final int MAX_ADDRESS_COUNT = 10;

    @Transactional
    public void createAddress(CreateAddressRequest request, Long memberId) {
        validateAddressCount(memberId);
        var member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        var addr = new MemberAddress(
                member,
                request.memo(),
                request.streetName(),
                request.detail(),
                request.zipcode()
        );
        memberAddressRepository.save(addr);
    }

    private void validateAddressCount(Long memberId) {
        int count = memberAddressRepository.countByMemberId(memberId);
        if (count >= MAX_ADDRESS_COUNT) throw new AddressLimitExceededException();
    }

    public List<AddressResponse> getAllAddresses(Long memberId) {
        return memberAddressRepository.findAllByMemberId(memberId)
                .stream().map(AddressResponse::from).toList();
    }

    public AddressResponse getAddress(Long memberId, Long addressId) {
        var entity = memberAddressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(AddressNotFoundException::new);
        return AddressResponse.from(entity);
    }

    @Transactional
    public void deleteAddress(Long memberId, Long addressId) {
        var entity = memberAddressRepository.findByIdAndMemberId(addressId, memberId)
                .orElseThrow(AddressNotFoundException::new);
        memberAddressRepository.delete(entity);
    }
}
