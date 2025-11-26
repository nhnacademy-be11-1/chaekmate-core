package shop.chaekmate.core.member.service;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.response.AddressResponse;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberAddress;
import shop.chaekmate.core.member.exception.AddressLimitExceededException;
import shop.chaekmate.core.member.exception.AddressNotFoundException;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberAddressRepository;
import shop.chaekmate.core.member.repository.MemberRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AddressServiceTest {

    @Mock
    MemberAddressRepository memberAddressRepository;

    @Mock
    MemberRepository memberRepository;

    @InjectMocks
    AddressService addressService;

    @Test
    void 주소_생성_성공() {
        Long memberId = 1L;
        var req = new CreateAddressRequest(
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );

        given(memberAddressRepository.countByMemberId(memberId)).willReturn(0);
        Member member = mock(Member.class);
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberAddressRepository.save(any(MemberAddress.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        addressService.createAddress(req, memberId);

        ArgumentCaptor<MemberAddress> captor = ArgumentCaptor.forClass(MemberAddress.class);
        verify(memberAddressRepository).save(captor.capture());

        MemberAddress saved = captor.getValue();

        assertAll(
                () -> assertThat(saved.getMember()).isEqualTo(member),
                () -> assertThat(saved.getMemo()).isEqualTo("집"),
                () -> assertThat(saved.getStreetName()).isEqualTo("대전 서구 대학로 99"),
                () -> assertThat(saved.getDetail()).isEqualTo("공대 4호관 101호"),
                () -> assertThat(saved.getZipcode()).isEqualTo(34134)
        );
    }


    @Test
    void 주소_생성_실패_최대개수_초과() {
        Long memberId = 1L;
        var req = new CreateAddressRequest(
                "회사",
                "서울 어느 길 1",
                "어딘가 202호",
                12345
        );

        given(memberAddressRepository.countByMemberId(memberId)).willReturn(10);

        assertThatThrownBy(() -> addressService.createAddress(req, memberId))
                .isInstanceOf(AddressLimitExceededException.class);

        verify(memberRepository, never()).findById(anyLong());
        verify(memberAddressRepository, never()).save(any(MemberAddress.class));
    }

    @Test
    void 주소_생성_실패_회원_없음() {
        Long memberId = 99L;
        var req = new CreateAddressRequest(
                "집",
                "어디",
                "어디 101호",
                11111
        );

        given(memberAddressRepository.countByMemberId(memberId)).willReturn(0);
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.createAddress(req, memberId))
                .isInstanceOf(MemberNotFoundException.class);

        verify(memberAddressRepository, never()).save(any(MemberAddress.class));
    }

    @Test
    void 주소_전체_조회_성공() {
        Long memberId = 1L;
        Member member = mock(Member.class);

        MemberAddress addr1 = new MemberAddress(
                member,
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );
        MemberAddress addr2 = new MemberAddress(
                member,
                "학교",
                "대전 유성구 대학로 1",
                "어딘가 202호",
                12345
        );

        given(memberAddressRepository.findAllByMemberId(memberId))
                .willReturn(List.of(addr1, addr2));

        List<AddressResponse> result = addressService.getAllAddresses(memberId);

        AddressResponse res1 = result.get(0);
        AddressResponse res2 = result.get(1);

        assertAll(
                () -> assertThat(result).hasSize(2),

                () -> assertThat(res1.memo()).isEqualTo("집"),
                () -> assertThat(res1.streetName()).isEqualTo("대전 서구 대학로 99"),
                () -> assertThat(res1.detail()).isEqualTo("공대 4호관 101호"),
                () -> assertThat(res1.zipcode()).isEqualTo(34134),

                () -> assertThat(res2.memo()).isEqualTo("학교"),
                () -> assertThat(res2.streetName()).isEqualTo("대전 유성구 대학로 1"),
                () -> assertThat(res2.detail()).isEqualTo("어딘가 202호"),
                () -> assertThat(res2.zipcode()).isEqualTo(12345)
        );
    }


    @Test
    void 단일_주소_조회_성공() {
        Long memberId = 1L;
        Long addressId = 10L;
        Member member = mock(Member.class);

        MemberAddress addr = new MemberAddress(
                member,
                "학교",
                "대전 유성구 대학로 99",
                "정보화본부",
                34134
        );

        given(memberAddressRepository.findByIdAndMemberId(addressId, memberId))
                .willReturn(Optional.of(addr));

        AddressResponse result = addressService.getAddress(memberId, addressId);

        assertAll(
                () -> assertThat(result.memo()).isEqualTo("학교"),
                () -> assertThat(result.streetName()).isEqualTo("대전 유성구 대학로 99"),
                () -> assertThat(result.detail()).isEqualTo("정보화본부"),
                () -> assertThat(result.zipcode()).isEqualTo(34134)
        );
    }


    @Test
    void 단일_주소_조회_실패_존재하지_않음() {
        Long memberId = 1L;
        Long addressId = 999L;

        given(memberAddressRepository.findByIdAndMemberId(addressId, memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.getAddress(memberId, addressId))
                .isInstanceOf(AddressNotFoundException.class);
    }

    @Test
    void 주소_삭제_성공() {
        Long memberId = 1L;
        Long addressId = 10L;
        Member member = mock(Member.class);

        MemberAddress addr = new MemberAddress(
                member,
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );

        given(memberAddressRepository.findByIdAndMemberId(addressId, memberId))
                .willReturn(Optional.of(addr));

        addressService.deleteAddress(memberId, addressId);

        verify(memberAddressRepository).delete(addr);
    }

    @Test
    void 주소_삭제_실패_주소_없음() {
        Long memberId = 1L;
        Long addressId = 999L;

        given(memberAddressRepository.findByIdAndMemberId(addressId, memberId))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> addressService.deleteAddress(memberId, addressId))
                .isInstanceOf(AddressNotFoundException.class);

        verify(memberAddressRepository, never()).delete(any(MemberAddress.class));
    }
}
