package shop.chaekmate.core.book.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
import shop.chaekmate.core.book.dto.request.BookImageAddRequest;
import shop.chaekmate.core.book.dto.request.ThumbnailUpdateRequest;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.BookImage;
import shop.chaekmate.core.book.repository.BookImageRepository;
import shop.chaekmate.core.book.repository.BookRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private BookImageRepository bookImageRepository;

    private Book book;

    @BeforeEach
    void setUp() {
        book = bookRepository.save(Book.builder()
                .isbn("1234567890")
                .title("Test Book")
                .author("Test Author")
                .publisher("Test Publisher")
                .index("Test Index")
                .description("Test Description")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0L)
                .isSaleEnd(false)
                .stock(10)
                .build());
    }

    @Test
    void 이미지_추가_API_성공() throws Exception {
        // given
        BookImageAddRequest request = new BookImageAddRequest("http://example.com/image.jpg");

        // when & then
        mockMvc.perform(post("/books/{bookId}/images", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.imageUrl").value("http://example.com/image.jpg"))
                .andExpect(jsonPath("$.data.isThumbnail").value(true))
                .andDo(print());
    }

    @Test
    void 이미지_추가_API_실패_잘못된_요청() throws Exception {
        // given
        BookImageAddRequest request = new BookImageAddRequest(" "); // Invalid URL

        // when & then
        mockMvc.perform(post("/books/{bookId}/images", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 썸네일_조회_API_성공() throws Exception {
        // given
        bookImageRepository.save(new BookImage(book, "http://thumb.url"));

        // when & then
        mockMvc.perform(get("/books/{bookId}/images/thumbnail", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.imageUrl").value("http://thumb.url"))
                .andExpect(jsonPath("$.data.isThumbnail").value(true));
    }

    @Test
    void 썸네일_조회_API_실패_썸네일_없음() throws Exception {
        // when & then
        mockMvc.perform(get("/books/{bookId}/images/thumbnail", book.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void 썸네일_수정_API_성공() throws Exception {
        // given
        bookImageRepository.save(new BookImage(book, "http://old.thumb"));
        ThumbnailUpdateRequest request = new ThumbnailUpdateRequest("http://new.thumb");

        // when & then
        mockMvc.perform(put("/books/{bookId}/images/thumbnail", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void 이미지_삭제_API_성공() throws Exception {
        // given
        BookImage image = bookImageRepository.save(new BookImage(book, "http://image.to.delete"));

        // when & then
        mockMvc.perform(delete("/books/{bookId}/images/{imageId}", book.getId(), image.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void 이미지_삭제_API_실패_존재하지_않는_이미지() throws Exception {
        // when & then
        mockMvc.perform(delete("/books/{bookId}/images/{imageId}", book.getId(), 999L))
                .andExpect(status().isNotFound());
    }
}
