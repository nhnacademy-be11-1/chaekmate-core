package shop.chaekmate.core.member.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.service.MemberService;

import static org.mockito.BDDMockito.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class MemberControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    MemberService memberService;

    @Test
    void 회원가입_성공() throws Exception {
        var body = createReq(
                "testUser",
                "Pw123456!",
                "홍길동",
                "01012345678",
                "test@example.com",
                LocalDate.of(2000, 1, 1)
        );

        mvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void 잘못된_입력값이면_400() throws Exception {
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
        given(memberService.isDuplicateLoginId("dupId")).willReturn(true);
        given(memberService.isDuplicateLoginId("newLoginId")).willReturn(false);

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

        mvc.perform(get("/members/check-login-id")
                        .param("loginId", "dupId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));

        mvc.perform(get("/members/check-login-id")
                        .param("loginId", "newLoginId"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void 이메일_중복_체크() throws Exception {
        given(memberService.isDuplicateEmail("dup-email@test.com")).willReturn(true);
        given(memberService.isDuplicateEmail("new-email@test.com")).willReturn(false);

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

        mvc.perform(get("/members/check-email")
                        .param("email", "dup-email@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(false));

        mvc.perform(get("/members/check-email")
                        .param("email", "new-email@test.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.available").value(true));
    }

    @Test
    void 회원_등급_조회_성공() throws Exception {
        Long memberId = 1L;
        GradeResponse grade = new GradeResponse("일반", (byte) 1, 0);

        given(memberService.getMemberGrade(memberId)).willReturn(grade);

        mvc.perform(get("/members/{memberId}/grade", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("일반"))
                .andExpect(jsonPath("$.data.pointRate").value(1))
                .andExpect(jsonPath("$.data.upgradeStandardAmount").value(0));
    }

    @Test
    void 전체_등급_목록_조회_성공() throws Exception {
        var grades = List.of(
                new GradeResponse("일반", (byte) 1, 0),
                new GradeResponse("로얄", (byte) 2, 100_000),
                new GradeResponse("골드", (byte) 3, 300_000),
                new GradeResponse("플래티넘", (byte) 5, 500_000)
        );

        given(memberService.getAllGrades()).willReturn(grades);

        mvc.perform(get("/members/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(4))
                .andExpect(jsonPath("$.data[0].name").value("일반"))
                .andExpect(jsonPath("$.data[1].name").value("로얄"))
                .andExpect(jsonPath("$.data[2].name").value("골드"))
                .andExpect(jsonPath("$.data[3].name").value("플래티넘"));
    }

    private CreateMemberRequest createReq(
            String loginId, String password, String name, String phone, String email, LocalDate birth
    ) {
        return new CreateMemberRequest(loginId, password, name, phone, email, birth);
    }
}
