package shop.chaekmate.core.book.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void 최근_추가된_도서_조회_요청_성공() throws Exception {
        bookRepository.save(
                new Book("test title", "testIndex", "testDesc", "testAuthor", "testPublisher", LocalDateTime.now(),
                        "1234567891123", 100, 100, false, 0, false, 0));

        mockMvc.perform(get("/admin/books/recent?limit=1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].title").value("test title"));
    }

    @Test
    void 관리자_도서_목록_조회_요청_성공() throws Exception{
        bookRepository.save(new Book("test title", "testIndex", "testDesc", "testAuthor", "testPublisher", LocalDateTime.now(),
                "1234567891123", 100, 100, false, 0, false, 0));

        mockMvc.perform(get("/admin/books/paged?limit=1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.content.[0].title").value("test title"));
    }
}
