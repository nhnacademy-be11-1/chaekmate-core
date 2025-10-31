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
import shop.chaekmate.core.book.dto.request.CreateCategoryRequest;
import shop.chaekmate.core.book.dto.request.UpdateCategoryRequest;
import shop.chaekmate.core.book.entity.Category;
import shop.chaekmate.core.book.repository.CategoryRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void 카테고리_생성_요청_성공() throws Exception {
        CreateCategoryRequest request = new CreateCategoryRequest(null, "New Category");

        mockMvc.perform(post("/admin/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Category"));
    }

    @Test
    void 모든_카테고리_조회_요청_성공() throws Exception {
        categoryRepository.save(new Category(null, "Test Category"));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Category"));
    }

    @Test
    void ID로_카테고리_조회_요청_성공() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Test Category"));

        mockMvc.perform(get("/categories/{id}", category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Test Category"));
    }

    @Test
    void 카테고리_수정_요청_성공() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Old Category"));
        UpdateCategoryRequest request = new UpdateCategoryRequest(null, "Updated Category");

        mockMvc.perform(put("/admin/categories/{id}", category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    void ID로_카테고리_삭제_요청_성공() throws Exception {
        Category category = categoryRepository.save(new Category(null, "Test Category"));

        mockMvc.perform(delete("/admin/categories/{id}", category.getId()))
                .andExpect(status().isNoContent());

        assertThat(categoryRepository.findById(category.getId())).isEmpty();
    }
}
