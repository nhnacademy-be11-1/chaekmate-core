package shop.chaekmate.core.book.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.dto.request.CreateTagRequest;
import shop.chaekmate.core.book.dto.request.UpdateTagRequest;
import shop.chaekmate.core.book.entity.Tag;
import shop.chaekmate.core.book.repository.TagRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@SuppressWarnings("NonAsciiCharacters")
@ActiveProfiles("test")
class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TagRepository tagRepository;

    @Test
    void 태그_생성_요청_성공() throws Exception {
        CreateTagRequest request = new CreateTagRequest("New Tag");

        mockMvc.perform(post("/admin/tags")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Tag"));
    }

    @Test
    void ID로_태그_조회_요청_성공() throws Exception {
        Tag tag = tagRepository.save(new Tag("Test Tag"));

        mockMvc.perform(get("/tags/{id}", tag.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tag.getId()))
                .andExpect(jsonPath("$.name").value("Test Tag"));
    }

    @Test
    void 모든_태그_조회_요청_성공() throws Exception {
        tagRepository.save(new Tag("Test Tag"));

        mockMvc.perform(get("/tags"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Tag"));
    }

    @Test
    void 태그_수정_요청_성공() throws Exception {
        Tag tag = tagRepository.save(new Tag("Old Tag"));
        UpdateTagRequest request = new UpdateTagRequest("Updated Tag");

        mockMvc.perform(put("/admin/tags/{id}", tag.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(tag.getId()))
                .andExpect(jsonPath("$.name").value("Updated Tag"));
    }

    @Test
    void ID로_태그_삭제_요청_성공() throws Exception {
        Tag tag = tagRepository.save(new Tag("Test Tag"));

        mockMvc.perform(delete("/admin/tags/{id}", tag.getId()))
                .andExpect(status().isNoContent());

        assertThat(tagRepository.findById(tag.getId())).isEmpty();
    }
}
