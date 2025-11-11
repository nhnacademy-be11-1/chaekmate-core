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
import shop.chaekmate.core.book.dto.request.BookCreateRequest;
import shop.chaekmate.core.book.dto.request.BookUpdateRequest;
import shop.chaekmate.core.book.entity.*;
import shop.chaekmate.core.book.repository.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
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
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookImageRepository bookImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private BookCategoryRepository bookCategoryRepository;

    @Autowired
    private BookTagRepository bookTagRepository;

    private Category category;
    private Tag tag;
    private Book book;

    @BeforeEach
    void setUp() {
        category = new Category(null, "프로그래밍");
        categoryRepository.save(category);

        tag = new Tag("베스트셀러");
        tagRepository.save(tag);

        book = Book.builder()
                .title("테스트 책")
                .index("목차")
                .description("설명")
                .author("저자")
                .publisher("출판사")
                .publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .isbn("9781234567890")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(100)
                .build();
        bookRepository.save(book);

        BookImage bookImage = new BookImage(book, "https://example.com/image.jpg");
        bookImageRepository.save(bookImage);

        BookCategory bookCategory = new BookCategory(book, category);
        bookCategoryRepository.save(bookCategory);

        BookTag bookTag = new BookTag(book, tag);
        bookTagRepository.save(bookTag);
    }

    @Test
    void 도서_생성_성공() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "스프링 부트 완벽 가이드",
                "1장. 스프링 부트 시작하기",
                "스프링 부트 개발 가이드",
                "김개발",
                "테크북스",
                LocalDateTime.of(2024, 3, 15, 0, 0, 0),
                "9781234567897",
                42000,
                37800,
                "https://example.com/books/spring-boot-guide.jpg",
                true,
                false,
                200,
                List.of(category.getId()),
                List.of(tag.getId())
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void 도서_생성_실패_유효성_검증_실패() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                "9781234567897",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(category.getId()),
                List.of(tag.getId())
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 도서_생성_실패_ISBN_중복() throws Exception {
        BookCreateRequest request = new BookCreateRequest(
                "중복 ISBN 책",
                "목차",
                "설명",
                "저자",
                "출판사",
                LocalDateTime.of(2024, 1, 1, 0, 0, 0),
                "9781234567890",
                10000,
                9000,
                "https://example.com/image.jpg",
                true,
                false,
                100,
                List.of(category.getId()),
                List.of(tag.getId())
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 도서_수정_성공() throws Exception {
        BookUpdateRequest request = new BookUpdateRequest(
                "수정된 책 제목",
                "수정된 목차",
                "수정된 설명",
                "수정된 저자",
                "수정된 출판사",
                LocalDateTime.of(2024, 2, 1, 0, 0, 0),
                "9780134685991",
                12000,
                10000,
                "https://example.com/new-image.jpg",
                false,
                true,
                50,
                List.of(category.getId()),
                List.of(tag.getId())
        );

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(put("/books/{bookId}", book.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 도서_수정_실패_책_없음() throws Exception {
        // GlobalException 적용 후 수정
    }

    @Test
    void 도서_삭제_성공() throws Exception {
        // 삭제 수정 테스트 반영 필요
    }

    @Test
    void 도서_삭제_실패_책_없음() throws Exception {
        // GlobalException 적용 후 수정
    }

    @Test
    void 도서_조회_성공() throws Exception {
        mockMvc.perform(get("/books/{bookId}", book.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(book.getId()))
                .andExpect(jsonPath("$.data.title").value("테스트 책"))
                .andExpect(jsonPath("$.data.author").value("저자"))
                .andExpect(jsonPath("$.data.publisher").value("출판사"))
                .andExpect(jsonPath("$.data.isbn").value("9781234567890"))
                .andExpect(jsonPath("$.data.price").value(10000))
                .andExpect(jsonPath("$.data.salesPrice").value(9000))
                .andExpect(jsonPath("$.data.imageUrl").value("https://example.com/image.jpg"))
                .andExpect(jsonPath("$.data.categoryIds").isArray())
                .andExpect(jsonPath("$.data.categoryIds", hasSize(1)))
                .andExpect(jsonPath("$.data.tagIds").isArray())
                .andExpect(jsonPath("$.data.tagIds", hasSize(1)));
    }

    @Test
    void 도서_조회_실패_책_없음() throws Exception {
        // GlobalException 적용 후 수정
    }

    @Test
    void 도서_목록_조회_성공_카테고리로_검색() throws Exception {
        mockMvc.perform(get("/books")
                        .param("categoryId", category.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].title").value("테스트 책"))
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.number").value(0));
    }

    @Test
    void 도서_목록_조회_성공_태그로_검색() throws Exception {
        mockMvc.perform(get("/books")
                        .param("tagId", tag.getId().toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void 도서_목록_조회_성공_키워드로_검색() throws Exception {
        mockMvc.perform(get("/books")
                        .param("keyword", "테스트")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.data.content[0].title").value(containsString("테스트")));
    }

    @Test
    void 도서_목록_조회_실패_검색조건_없음() throws Exception {
        // GlobalException 적용 후 수정
    }

    @Test
    void 도서_목록_조회_실패_검색조건_2개_이상() throws Exception {
        // GlobalException 적용 후 수정
    }

    @Test
    void 도서_목록_조회_페이징_확인() throws Exception {
        Book book2 = Book.builder()
                .title("두 번째 책")
                .index("목차2")
                .description("설명2")
                .author("저자2")
                .publisher("출판사2")
                .publishedAt(LocalDateTime.of(2024, 2, 1, 0, 0))
                .isbn("9782345678901")
                .price(15000)
                .salesPrice(13500)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(50)
                .build();
        bookRepository.save(book2);

        BookImage bookImage2 = new BookImage(book2, "https://example.com/image2.jpg");
        bookImageRepository.save(bookImage2);

        BookCategory bookCategory2 = new BookCategory(book2, category);
        bookCategoryRepository.save(bookCategory2);

        mockMvc.perform(get("/books")
                        .param("categoryId", category.getId().toString())
                        .param("page", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.totalElements").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.totalPages").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$.data.size").value(1));
    }
}
