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
class MemberControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        var body = createReq(
                "testUser",
                "Pw123456!",
                "홍길동",
                "01012345678",
                "test@example.com",
                LocalDate.of(2000, 1, 1)
        );

        // when & then
        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());
        // 현재 컨트롤러는 바디를 안 돌려주므로 status만 검증
    }

    @Test
    void 잘못된_입력값이면_400() throws Exception {
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
    void 로그인아이디_중복_체크() throws Exception {
        // given: 먼저 한 명 가입
        var body = createReq(
                "dupId",
                "Pw123456!",
                "홍길동",
                "01011112222",
                "dup@test.com",
                LocalDate.of(2000, 1, 1)
        );

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // when & then
        // 1) 이미 존재하는 아이디 -> available = false 여야 함
        mvc.perform(get("/members/check-login-id")
                        .param("loginId", "dupId"))
                .andExpect(status().isOk())
                // 공통 응답 래퍼가 있으면 $.data.available, 없으면 $.available로 바꾸세요
                .andExpect(jsonPath("$.data.available").value(false));

        // 2) 존재하지 않는 아이디 -> available = true 여야 함
        mvc.perform(get("/members/check-login-id")
                        .param("loginId", "newLoginId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void 이메일_중복_체크() throws Exception {
        // given: 먼저 한 명 가입
        var body = createReq(
                "emailUser",
                "Pw123456!",
                "이영희",
                "01099998888",
                "dup-email@test.com",
                LocalDate.of(1999, 12, 31)
        );

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // when & then
        // 1) 이미 존재하는 이메일 -> available = false
        mvc.perform(get("/members/check-email")
                        .param("email", "dup-email@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));

        // 2) 존재하지 않는 이메일 -> available = true
        mvc.perform(get("/members/check-email")
                        .param("email", "new-email@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    private CreateMemberRequest createReq(
            String loginId, String password, String name, String phone, String email, LocalDate birth
    ) {
        return new CreateMemberRequest(loginId, password, name, phone, email, birth);
    }
}
