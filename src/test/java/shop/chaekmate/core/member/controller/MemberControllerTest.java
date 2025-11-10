package shop.chaekmate.core.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class MemberControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void 회원가입_성공() throws Exception {
        var body = createReq("test", "password", "name", "01012345678", "j@test.com", LocalDate.of(2003,5,1));

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.loginId").value("test"))
                .andExpect(jsonPath("$.data.email").value("j@test.com"));
    }

    @Test
    void 잘못된_입력값() throws Exception {
        // NotBlank/Email 등 깨뜨려서 검증 실패 유도
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

    @Test
    void 회원_조회_성공() throws Exception {
        long id = createMemberAndGetId(
                createReq("user1", "Pw123456!", "홍길동", "01011112222", "user1@test.com", LocalDate.of(2000,1,1))
        );

        mvc.perform(get("/members/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loginId").value("user1"))
                .andExpect(jsonPath("$.data.email").value("user1@test.com"));
    }

    @Test
    void 회원_조회_실패_없는_아이디() throws Exception {
        mvc.perform(get("/members/{id}", 99999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 전체_조회_성공() throws Exception {
        createMemberAndGetId(createReq("u1", "Pw123456!", "가", "01011112222", "u1@test.com", LocalDate.of(2000,1,1)));
        createMemberAndGetId(createReq("u2", "Pw123456!", "나", "01022223333", "u2@test.com", LocalDate.of(2001,2,2)));

        mvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].loginId").exists());
    }

    @Test
    void 회원_삭제후_조회시_실패() throws Exception {
        long id = createMemberAndGetId(
                createReq("3", "Pw123456!", "이영희", "01077778888", "user3@test.com", LocalDate.of(2001,2,3))
        );

        mvc.perform(delete("/members/{id}", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/members/{id}", id))
                .andExpect(status().isNotFound());
    }

    private CreateMemberRequest createReq(
            String loginId, String password, String name, String phone, String email, LocalDate birth
    ) {
        return new CreateMemberRequest(loginId, password, name, phone, email, birth);
    }

    private long createMemberAndGetId(CreateMemberRequest req) throws Exception {
        var res = mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode node = om.readTree(res.getResponse().getContentAsString());
        return node.get("data").get("id").asLong();
    }
}
