package shop.chaekmate.core.member.controller;

import static org.mockito.BDDMockito.*;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.service.AdminMemberService;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminMemberControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    AdminMemberService adminMemberService;

    @Test
    void 활성_회원_목록_조회_성공() throws Exception {
        var members = List.of(
                new MemberResponse(
                        1L,
                        "user1",
                        "홍길동",
                        "010-1111-2222",
                        "user1@test.com",
                        LocalDate.of(2000, 1, 1),
                        PlatformType.LOCAL,
                        null
                ),
                new MemberResponse(
                        2L,
                        "user2",
                        "김철수",
                        "010-3333-4444",
                        "user2@test.com",
                        LocalDate.of(1999, 5, 10),
                        PlatformType.LOCAL,
                        null
                )
        );

        given(adminMemberService.getActiveMembers()).willReturn(members);

        mvc.perform(get("/admin/members")
                        .param("status", "ACTIVE")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].loginId").value("user1"))
                .andExpect(jsonPath("$.data[0].name").value("홍길동"))
                .andExpect(jsonPath("$.data[1].loginId").value("user2"))
                .andExpect(jsonPath("$.data[1].name").value("김철수"));

        then(adminMemberService).should().getActiveMembers();
        then(adminMemberService).should(never()).getDeletedMembers();
    }

    @Test
    void 탈퇴_회원_목록_조회_성공() throws Exception {
        var members = List.of(
                new MemberResponse(
                        3L,
                        "deleted1",
                        "이삭제",
                        "010-5555-6666",
                        "deleted1@test.com",
                        LocalDate.of(1995, 3, 15),
                        PlatformType.LOCAL,
                        null
                )
        );

        given(adminMemberService.getDeletedMembers()).willReturn(members);

        mvc.perform(get("/admin/members")
                        .param("status", "DELETED")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].loginId").value("deleted1"))
                .andExpect(jsonPath("$.data[0].name").value("이삭제"));

        then(adminMemberService).should().getDeletedMembers();
        then(adminMemberService).should(never()).getActiveMembers();
    }

    @Test
    void 회원_삭제_성공() throws Exception {
        Long memberId = 10L;

        mvc.perform(delete("/admin/members/{memberId}", memberId))
                .andExpect(status().isOk());

        then(adminMemberService).should()
                .deleteMember(memberId);
    }

    @Test
    void 탈퇴_회원_복구_성공() throws Exception {
        Long memberId = 10L;

        mvc.perform(post("/admin/members/{memberId}/restore", memberId))
                .andExpect(status().isOk());

        then(adminMemberService).should()
                .restoreMember(memberId);
    }
}
