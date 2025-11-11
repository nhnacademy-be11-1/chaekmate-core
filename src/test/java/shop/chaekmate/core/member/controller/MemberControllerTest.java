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
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @Autowired MemberRepository memberRepository;

    @Test
    void 아이디_중복_체크() throws Exception {
        saveMember("dupId", "dup@test.com");

        mvc.perform(get("/members/check-login-id").param("loginId", "dupId").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));

        mvc.perform(get("/members/check-login-id").param("loginId", "newId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void 이메일_중복_체크() throws Exception {
        saveMember("u1", "user1@test.com");

        mvc.perform(get("/members/check-email").param("email", "user1@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));

        mvc.perform(get("/members/check-email").param("email", "free@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void 회원가입_성공() throws Exception {
        var body = createReq("test", "password123", "홍길동", "01012345678", "j@test.com",
                LocalDate.of(2003,5,1));

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void 회원가입_검증_실패() throws Exception {
        var badJson = """
            {
              "loginId": "",
              "password": "",
              "name": "",
              "phone": "010",
              "email": "not-email",
              "birthDate": "2010-01-01"
            }
            """;

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest());
    }

    // ----------------- helpers -----------------

    private void saveMember(String loginId, String email) {
        Member m = new Member(
                loginId,
                "{noop}pw",
                "이름",
                "01011112222",
                email,
                LocalDate.of(2000,1,1),
                PlatformType.LOCAL
        );
        memberRepository.saveAndFlush(m);
    }

    private CreateMemberRequest createReq(
            String loginId, String password, String name, String phone, String email, LocalDate birth
    ) {
        return new CreateMemberRequest(loginId, password, name, phone, email, birth);
    }
}
