package shop.chaekmate.core.point.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.repository.PointPolicyRepository;

//테스트 작성
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointPolicyRepository pointPolicyRepository;

    @Test
    void 포인트_정책_등록() throws Exception {
        CreatePointPolicyRequest request = new CreatePointPolicyRequest(PointEarnedType.WELCOME, 5000);

        mockMvc.perform(post("/admin/point-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.pointEarnedType").value("WELCOME"))
                .andExpect(jsonPath("$.data.point").value(5000));
    }

    @Test
    void 포인트_정책_회원용_조회() throws Exception {
        PointPolicy policy = pointPolicyRepository.save(new PointPolicy(PointEarnedType.ORDER, 100));

        mockMvc.perform(get("/point-policies/{type}", "ORDER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(policy.getId()))
                .andExpect(jsonPath("$.data.earnType").value("ORDER"))
                .andExpect(jsonPath("$.data.point").value(100));
    }

    @Test
    void 포인트_정책_관리자용_조회() throws Exception {
        PointPolicy policy = pointPolicyRepository.save(new PointPolicy(PointEarnedType.IMAGE_REVIEW, 500));

        mockMvc.perform(get("/admin/point-policies/{type}", "IMAGE_REVIEW"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(policy.getId()))
                .andExpect(jsonPath("$.data.earnType").value("IMAGE_REVIEW"))
                .andExpect(jsonPath("$.data.point").value(500));
    }

    @Test
    void 포인트_정책_수정() throws Exception {
        PointPolicy policy = pointPolicyRepository.save(new PointPolicy(PointEarnedType.ORDER, 100));
        UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(PointEarnedType.ORDER, 500);

        mockMvc.perform(put("/admin/point-policies/{type}", "ORDER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(policy.getId()))
                .andExpect(jsonPath("$.data.earnedType").value("ORDER"))
                .andExpect(jsonPath("$.data.point").value(500));

        PointPolicy updated = pointPolicyRepository.findByType(PointEarnedType.ORDER).orElseThrow();
        assertThat(updated.getPoint()).isEqualTo(500);
    }

    @Test
    void 포인트_정책_삭제() throws Exception {
        pointPolicyRepository.save(new PointPolicy(PointEarnedType.WELCOME, 5000));

        mockMvc.perform(delete("/admin/point-policies/{type}", "WELCOME"))
                .andExpect(status().isNoContent());

        assertThat(pointPolicyRepository.findByType(PointEarnedType.WELCOME)).isEmpty();
    }

    @Test
    void 중복_포인트_정책_등록_실패() throws Exception {
        pointPolicyRepository.save(new PointPolicy(PointEarnedType.ORDER, 100));
        CreatePointPolicyRequest request = new CreatePointPolicyRequest(PointEarnedType.ORDER, 200);

        mockMvc.perform(post("/admin/point-policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }
}
