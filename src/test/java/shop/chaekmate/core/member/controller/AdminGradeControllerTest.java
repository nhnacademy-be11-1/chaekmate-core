package shop.chaekmate.core.member.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.service.AdminGradeService;
import shop.chaekmate.core.member.service.MemberService;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminGradeControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    AdminGradeService adminGradeService;

    @MockitoBean
    MemberService memberService;

    @Test
    void 등급_전체_조회_성공() throws Exception {
        var grades = List.of(
                new GradeResponse(1L, "일반", (byte) 1, 0),
                new GradeResponse(2L, "로얄", (byte) 2, 100_000),
                new GradeResponse(3L, "골드", (byte) 3, 300_000)
        );

        given(memberService.getAllGrades()).willReturn(grades);

        mvc.perform(get("/admin/grades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(3))
                .andExpect(jsonPath("$.data[0].name").value("일반"))
                .andExpect(jsonPath("$.data[1].name").value("로얄"))
                .andExpect(jsonPath("$.data[2].name").value("골드"));
    }

    @Test
    void 등급_생성_성공() throws Exception {
        CreateGradeRequest req = new CreateGradeRequest("실버", (byte) 2, 50_000);

        mvc.perform(post("/admin/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        then(adminGradeService).should()
                .createGrade(refEq(req));
    }

    @Test
    void 등급_수정_성공() throws Exception {
        Long gradeId = 1L;
        UpdateGradeRequest req = new UpdateGradeRequest("플래티넘", (byte) 5, 500_000);

        mvc.perform(put("/admin/grades/{gradeId}", gradeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isNoContent());

        then(adminGradeService).should()
                .updateGrade(eq(gradeId), refEq(req));
    }

    @Test
    void 등급_삭제_성공() throws Exception {
        Long gradeId = 1L;

        mvc.perform(delete("/admin/grades/{gradeId}", gradeId))
                .andExpect(status().isNoContent());

        then(adminGradeService).should()
                .deleteGrade(gradeId);
    }
}
