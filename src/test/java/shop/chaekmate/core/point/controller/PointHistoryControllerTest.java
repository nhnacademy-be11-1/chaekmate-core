package shop.chaekmate.core.point.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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

import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.point.entity.PointHistory;
import shop.chaekmate.core.point.entity.type.PointSpendType;
import shop.chaekmate.core.point.repository.PointHistoryRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member testMember1;
    private Member testMember2;

    @BeforeEach
    void setUp() {
        testMember1 = memberRepository.save(new Member(
                "user1",
                "password123",
                "홍길동",
                "010-1234-5678",
                "user1@test.com",
                LocalDate.of(1990, 1, 1),
                PlatformType.LOCAL
        ));

        testMember2 = memberRepository.save(new Member(
                "user2",
                "password456",
                "김철수",
                "010-9876-5432",
                "user2@test.com",
                LocalDate.of(1995, 5, 15),
                PlatformType.PAYCO
        ));
    }

    @Test
    void 포인트_히스토리_전체_조회() throws Exception {
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 5000, "회원가입"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 100, "주문적립"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.SPEND, 3000, "주문사용"));
        pointHistoryRepository.save(new PointHistory(testMember2, PointSpendType.EARN, 500, "리뷰작성"));
        pointHistoryRepository.save(new PointHistory(testMember2, PointSpendType.SPEND, 200, "쿠폰구매"));

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.content[0].id").isNumber())
                .andExpect(jsonPath("$.data.content[0].type").exists())
                .andExpect(jsonPath("$.data.content[0].point").exists())
                .andExpect(jsonPath("$.data.content[0].source").exists());
    }

    @Test
    void 포인트_히스토리_페이징_조회() throws Exception {
        for (int i = 1; i <= 25; i++) {
            pointHistoryRepository.save(new PointHistory(
                    testMember1,
                    i % 2 == 0 ? PointSpendType.EARN : PointSpendType.SPEND,
                    i * 100,
                    "테스트" + i
            ));
        }

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.size").value(10))
                .andExpect(jsonPath("$.data.totalElements").value(25))
                .andExpect(jsonPath("$.data.totalPages").value(3))
                .andExpect(jsonPath("$.data.number").value(0));

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.number").value(1))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    void 포인트_히스토리_빈_목록_조회() throws Exception {
        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    void 포인트_히스토리_적립만_조회() throws Exception {
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 5000, "회원가입"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 100, "주문적립"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 500, "리뷰작성"));

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content[0].type").value("EARN"))
                .andExpect(jsonPath("$.data.content[1].type").value("EARN"))
                .andExpect(jsonPath("$.data.content[2].type").value("EARN"));
    }

    @Test
    void 포인트_히스토리_차감만_조회() throws Exception {
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.SPEND, 3000, "주문사용"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.SPEND, 1000, "쿠폰구매"));

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].type").value("SPEND"))
                .andExpect(jsonPath("$.data.content[1].type").value("SPEND"));
    }

    @Test
    void 여러_회원의_포인트_히스토리_조회() throws Exception {
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.EARN, 5000, "회원가입"));
        pointHistoryRepository.save(new PointHistory(testMember2, PointSpendType.EARN, 5000, "회원가입"));
        pointHistoryRepository.save(new PointHistory(testMember1, PointSpendType.SPEND, 1000, "주문사용"));
        pointHistoryRepository.save(new PointHistory(testMember2, PointSpendType.SPEND, 2000, "주문사용"));

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(4))
                .andExpect(jsonPath("$.data.content[0].member").exists())
                .andExpect(jsonPath("$.data.content[1].member").exists());

        assertThat(pointHistoryRepository.findAll()).hasSize(4);
    }

    @Test
    void 포인트_히스토리_기본_페이지_크기_확인() throws Exception {
        for (int i = 1; i <= 30; i++) {
            pointHistoryRepository.save(new PointHistory(
                    testMember1,
                    PointSpendType.EARN,
                    i * 100,
                    "테스트" + i
            ));
        }

        mockMvc.perform(get("/admin/point-histories")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size").value(20))
                .andExpect(jsonPath("$.data.totalElements").value(30))
                .andExpect(jsonPath("$.data.content").isArray());
    }
}