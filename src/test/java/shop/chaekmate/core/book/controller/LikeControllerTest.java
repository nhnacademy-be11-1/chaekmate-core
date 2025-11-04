package shop.chaekmate.core.book.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.entity.Like;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.book.repository.LikeRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;

    private Member member;
    private Book book;
    private String token;

    @BeforeEach
    void setUp() {
        Member newMember = new Member("test-login-id", "test-password", "test-name", "010-1234-5678", "test@email.com",
                LocalDate.now(), PlatformType.LOCAL);
        member = memberRepository.save(newMember);

        Book newBook = new Book("Test Book", "index", "description", "author", "publisher", LocalDateTime.now(),
                "1234567890123", 10000, 9000, true, 0, false, 10);
        book = bookRepository.save(newBook);

        token = "Bearer " + Jwts.builder()
                .claim("memberId", member.getId())
                .signWith(Keys.hmacShaKeyFor(
                        "chaekmatedummykeychaekmatedummykeychaekmatedummykey".getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Test
    void 좋아요_생성_요청_성공() throws Exception {
        mockMvc.perform(post("/books/{bookId}/likes", book.getId())
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.bookId").value(book.getId()))
                .andExpect(jsonPath("$.data.memberId").value(member.getId()));
    }

    @Test
    void ID로_좋아요_조회_요청_성공() throws Exception {
        Like like = likeRepository.save(new Like(book, member));

        mockMvc.perform(get("/likes/{likeId}", like.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(like.getId()));
    }

    @Test
    void 책_ID로_좋아요_목록_조회_요청_성공() throws Exception {
        likeRepository.save(new Like(book, member));

        mockMvc.perform(get("/books/{bookId}/likes", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].bookId").value(book.getId()));
    }

    @Test
    void 회원_ID로_좋아요_목록_조회_요청_성공() throws Exception {
        likeRepository.save(new Like(book, member));

        mockMvc.perform(get("/members/likes")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].memberId").value(member.getId()));
    }

    @Test
    void ID로_좋아요_삭제_요청_성공() throws Exception {
        Like like = likeRepository.save(new Like(book, member));

        mockMvc.perform(delete("/likes/{likeId}", like.getId()))
                .andExpect(status().isNoContent());

        assertThat(likeRepository.findById(like.getId())).isEmpty();
    }

    @Test
    void 책_및_회원_ID로_좋아요_삭제_요청_성공() throws Exception {
        Like like = likeRepository.save(new Like(book, member));

        mockMvc.perform(delete("/books/{bookId}/likes", book.getId())
                        .header("Authorization", token))
                .andExpect(status().isNoContent());

        assertThat(likeRepository.findById(like.getId())).isEmpty();
    }
}
