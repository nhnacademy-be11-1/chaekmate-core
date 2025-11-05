package shop.chaekmate.core.order.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
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
import shop.chaekmate.core.order.dto.request.WrapperRequest;
import shop.chaekmate.core.order.entity.Wrapper;
import shop.chaekmate.core.order.repository.WrapperRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class WrapperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WrapperRepository wrapperRepository;

    @Test
    void 포장지_등록_성공() throws Exception {
        WrapperRequest request = new WrapperRequest("테스트 포장지", 1000);

        mockMvc.perform(post("/admin/wrappers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.name").value(request.name()))
                .andExpect(jsonPath("$.data.price").value(request.price()));
    }

    @Test
    void 포장지_수정_성공() throws Exception {
        Wrapper wrapper = wrapperRepository.save(new Wrapper("테스트 포장지", 1000));
        WrapperRequest request = new WrapperRequest("수정된 포장지", 2000);

        mockMvc.perform(put("/admin/wrappers/{id}", wrapper.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(request.name()))
                .andExpect(jsonPath("$.data.price").value(request.price()));
    }

    @Test
    void 포장지_삭제_성공() throws Exception {
        Wrapper wrapper = wrapperRepository.save(new Wrapper("테스트 포장지", 1000));

        mockMvc.perform(delete("/admin/wrappers/{id}", wrapper.getId()))
                .andExpect(status().isNoContent());

        Optional<Wrapper> deleteWrapper = wrapperRepository.findById(wrapper.getId());
        assertThat(deleteWrapper).isEmpty();
    }

    @Test
    void 포장지_단일_조회_성공() throws Exception {
        Wrapper wrapper = wrapperRepository.save(new Wrapper("테스트 포장지", 1000));

        mockMvc.perform(get("/wrappers/{id}", wrapper.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(wrapper.getId()))
                .andExpect(jsonPath("$.data.name").value(wrapper.getName()))
                .andExpect(jsonPath("$.data.price").value(wrapper.getPrice()));
    }

    @Test
    void 포장지_전체_조회_성공() throws Exception {
        Wrapper wrapper1 = wrapperRepository.save(new Wrapper("포장지1", 1000));
        Wrapper wrapper2 = wrapperRepository.save(new Wrapper("포장지2", 2000));

        mockMvc.perform(get("/wrappers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[*].name").value(
                        org.hamcrest.Matchers.containsInAnyOrder(wrapper1.getName(), wrapper2.getName())))
                .andExpect(
                        jsonPath("$.data[*].price").value(org.hamcrest.Matchers.containsInAnyOrder(wrapper1.getPrice(),
                                wrapper2.getPrice())));
    }
}
