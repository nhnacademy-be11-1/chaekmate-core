package shop.chaekmate.core.review.controller;

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
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.order.repository.OrderedBookRepository;
import shop.chaekmate.core.review.dto.request.ReviewImageAddRequest;
import shop.chaekmate.core.review.entity.Review;
import shop.chaekmate.core.review.entity.ReviewImage;
import shop.chaekmate.core.review.repository.ReviewImageRepository;
import shop.chaekmate.core.review.repository.ReviewRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
class ReviewImageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewImageRepository reviewImageRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderedBookRepository orderedBookRepository;

    private Review review;

    @BeforeEach
    void setUp() {
        Member member = memberRepository.save(new Member("id1", "pwd", "name1", "0101", "e1@mail.com", LocalDate.now(), PlatformType.LOCAL));
        Book book = bookRepository.save(Book.builder()
                .title("title")
                .publishedAt(LocalDateTime.now())
                .isbn("1234567890123")
                .views(0)
                .stock(10)
                .index("index")
                .description("description")
                .author("author")
                .publisher("publisher")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .isSaleEnd(false)
                .build());
        Order order = orderRepository.save(Order.createOrderReady(member, "order1", "n", "p", "e", "r", "p", "z", "s", "d", "r", LocalDate.now(), 0, 9000));
        OrderedBook orderedBook = orderedBookRepository.save(OrderedBook.createOrderDetailReady(order, book, 1, 10000, 9000, 0, null, 0, null, 0, 0, 9000, 9000));
        review = reviewRepository.save(Review.createReview(member, orderedBook, "comment", 5));
    }

    @Test
    void 리뷰_이미지_추가_API_성공() throws Exception {
        // given
        ReviewImageAddRequest request = new ReviewImageAddRequest(List.of("url1", "url2"));

        // when & then
        mockMvc.perform(post("/reviews/{reviewId}/images", review.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].imageUrl").value("url1"))
                .andDo(print());
    }

    @Test
    void 리뷰_이미지_조회_API_성공() throws Exception {
        // given
        reviewImageRepository.save(new ReviewImage(review, "url1"));
        reviewImageRepository.save(new ReviewImage(review, "url2"));

        // when & then
        mockMvc.perform(get("/reviews/{reviewId}/images", review.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andDo(print());
    }

    @Test
    void 리뷰_이미지_삭제_API_성공() throws Exception {
        // given
        ReviewImage image = reviewImageRepository.save(new ReviewImage(review, "url1"));

        // when & then
        mockMvc.perform(delete("/reviews/{reviewId}/images/{imageId}", review.getId(), image.getId()))
                .andExpect(status().isOk());
    }
}
