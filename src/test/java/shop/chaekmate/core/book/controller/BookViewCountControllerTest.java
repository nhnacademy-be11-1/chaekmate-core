package shop.chaekmate.core.book.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.service.BookViewCountService;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookViewCountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookViewCountService bookViewCountService;

    @Test
    void 조회수_증가_API_성공() throws Exception {
        // given
        Long bookId = 1L;

        // Service 동작 stub
        doNothing().when(bookViewCountService).increase(bookId);

        // when & then
        mockMvc.perform(post("/books/{bookId}/views", bookId))
                .andExpect(status().isOk());

        // Service 호출 검증
        verify(bookViewCountService, times(1)).increase(bookId);
    }
}
