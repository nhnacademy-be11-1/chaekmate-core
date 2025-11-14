package shop.chaekmate.core.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.MemberAddress;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberAddressRepository;
import shop.chaekmate.core.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AddressControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired MemberRepository memberRepository;
    @Autowired MemberAddressRepository memberAddressRepository;

    @Test
    void 주소_생성_성공() throws Exception {
        Long memberId = saveMember("user1", "user1@test.com").getId();

        var body = new CreateAddressRequest(
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );

        mvc.perform(post("/members/{memberId}/addresses", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void 주소_생성_검증_실패() throws Exception {
        Long memberId = saveMember("user1", "user1@test.com").getId();

        String badJson = """
            {
              "memo": "",
              "streetName": "",
              "detail": "",
              "zipcode": 0
            }
            """;

        mvc.perform(post("/members/{memberId}/addresses", memberId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 주소_전체_조회_성공() throws Exception {
        Member member = saveMember("user1", "user1@test.com");
        Long memberId = member.getId();

        saveAddress(member, "집", "대전 서구 대학로 99", "공대 4호관 101호", 34134);
        saveAddress(member, "학교", "대전 유성구 대학로 1", "어딘가 202호", 12345);

        mvc.perform(get("/members/{memberId}/addresses", memberId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 단일_주소_조회_성공() throws Exception {
        Member member = saveMember("user1", "user1@test.com");
        Long memberId = member.getId();

        MemberAddress addr = saveAddress(
                member,
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );

        mvc.perform(get("/members/{memberId}/addresses/{addressId}", memberId, addr.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void 주소_삭제_성공() throws Exception {
        Member member = saveMember("user1", "user1@test.com");
        Long memberId = member.getId();

        MemberAddress addr = saveAddress(
                member,
                "집",
                "대전 서구 대학로 99",
                "공대 4호관 101호",
                34134
        );

        mvc.perform(delete("/members/{memberId}/addresses/{addressId}", memberId, addr.getId()))
                .andExpect(status().isNoContent());
    }

    private Member saveMember(String loginId, String email) {
        Member m = new Member(
                loginId,
                "{noop}pw",
                "이름",
                "01011112222",
                email,
                LocalDate.of(2000, 1, 1),
                PlatformType.LOCAL
        );
        return memberRepository.saveAndFlush(m);
    }

    private MemberAddress saveAddress(Member member, String memo, String streetName, String detail, int zipcode) {
        MemberAddress addr = new MemberAddress(
                member,
                memo,
                streetName,
                detail,
                zipcode
        );
        return memberAddressRepository.saveAndFlush(addr);
    }
}
