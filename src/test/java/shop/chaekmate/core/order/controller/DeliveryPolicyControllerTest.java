package shop.chaekmate.core.order.controller;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static shop.chaekmate.core.common.TestRequestPostProcessors.asAdmin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyRequest;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeliveryPolicyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @Test
    void 배달_정책_등록() throws Exception {
        DeliveryPolicyRequest request = new DeliveryPolicyRequest(30000, 5000);

        mockMvc.perform(post("/admin/delivery-policy")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.freeStandardAmount").value(request.freeStandardAmount()))
                .andExpect(jsonPath("$.data.deliveryFee").value(request.deliveryFee()));
    }

    @Test
    void 배달_정책_변경_삭제_후_등록() throws Exception {
        DeliveryPolicyRequest request1 = new DeliveryPolicyRequest(30000, 5000);
        DeliveryPolicyRequest request2 = new DeliveryPolicyRequest(50000, 3000);

        mockMvc.perform(post("/admin/delivery-policy")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/admin/delivery-policy")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.freeStandardAmount").value(request2.freeStandardAmount()))
                .andExpect(jsonPath("$.data.deliveryFee").value(request2.deliveryFee()));

        List<DeliveryPolicy> policies =
                deliveryPolicyRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        assertThat(policies).hasSize(2);
        assertThat(policies.get(0).getFreeStandardAmount()).isEqualTo(50000);
        assertThat(policies.get(0).getDeliveryFee()).isEqualTo(3000);

        assertThat(policies.get(1).getDeletedAt()).isNotNull();
    }

    @Test
    void 배달_정책_기록_조회() throws Exception {
        deliveryPolicyRepository.save(new DeliveryPolicy(30000, 5000));
        deliveryPolicyRepository.save(new DeliveryPolicy(40000, 4000));
        deliveryPolicyRepository.save(new DeliveryPolicy(50000, 3000));

        mockMvc.perform(get("/admin/delivery-policy")
                        .param("page", "0")
                        .param("size", "20")
                        .with(asAdmin())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content[0].freeStandardAmount").value(50000))
                .andExpect(jsonPath("$.data.content[1].deliveryFee").value(4000));
    }

    @Test
    void 현재_배달_정책_조회() throws Exception {
        DeliveryPolicy deliveryPolicy = deliveryPolicyRepository.save(new DeliveryPolicy(30000, 5000));

        mockMvc.perform(get("/delivery-policy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(deliveryPolicy.getId()))
                .andExpect(jsonPath("$.data.freeStandardAmount").value(deliveryPolicy.getFreeStandardAmount()))
                .andExpect(jsonPath("$.data.deliveryFee").value(deliveryPolicy.getDeliveryFee()));
    }
}
