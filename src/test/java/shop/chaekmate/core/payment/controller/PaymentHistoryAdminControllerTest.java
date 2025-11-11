package shop.chaekmate.core.payment.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.OffsetDateTime;
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
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Test
    void 전체_결제_내역_조회() throws Exception {
        Payment payment1 = paymentRepository.save(
                Payment.createApproved("test_order_number1", "test_payment_key_random1", PaymentMethodType.TOSS,
                        29800L, null)
        );
        Payment payment2 = paymentRepository.save(
                Payment.createApproved("test_order_number_2", "test_payment_key_random2", PaymentMethodType.POINT,
                        15000L, null)
        );
        Payment payment3 = paymentRepository.save(
                Payment.createApproved("test_order_number_3", "test_payment_key_random3", PaymentMethodType.POINT,
                        0L, 10000)
        );
        paymentHistoryRepository.save(PaymentHistory.approved(payment1, 29800L, OffsetDateTime.now()));
        paymentHistoryRepository.save(PaymentHistory.approved(payment2, 15000L, OffsetDateTime.now()));
        paymentHistoryRepository.save(PaymentHistory.approved(payment3, 10000L, OffsetDateTime.now()));

        mockMvc.perform(get("/admin/payments/histories")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(3))
                .andExpect(jsonPath("$.data.content[0].orderNumber").value("test_order_number_3"))
                .andExpect(jsonPath("$.data.content[1].paymentType").exists());
    }

    @Test
    void 결제수단별_전체_조회() throws Exception {
        Payment toss1 = paymentRepository.save(
                Payment.createApproved("test_order_number1", "test_payment_key_random1", PaymentMethodType.TOSS, 10000L,
                        null)
        );
        Payment toss2 = paymentRepository.save(
                Payment.createApproved("test_order_number2", "test_payment_key_random2", PaymentMethodType.TOSS, 20000L,
                        null)
        );
        Payment point = paymentRepository.save(
                Payment.createApproved("test_order_number3", "test_payment_key_random3", PaymentMethodType.POINT, 0,
                        5000)
        );

        paymentHistoryRepository.save(PaymentHistory.approved(toss1, 10000L, OffsetDateTime.now()));
        paymentHistoryRepository.save(PaymentHistory.approved(toss2, 20000L, OffsetDateTime.now()));
        paymentHistoryRepository.save(PaymentHistory.approved(point, 5000L, OffsetDateTime.now()));

        mockMvc.perform(get("/admin/payments/histories")
                        .param("paymentType", PaymentMethodType.TOSS.name())
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].paymentType").value("TOSS"))
                .andExpect(jsonPath("$.data.content[1].paymentType").value("TOSS"));
    }

    @Test
    void 모든_결제수단_기간별_조회() throws Exception {
        OffsetDateTime start = OffsetDateTime.now().minusDays(5);
        OffsetDateTime end = OffsetDateTime.now().plusDays(1);

        Payment toss = paymentRepository.save(
                Payment.createApproved("test_order_number1", "test_payment_key_random1", PaymentMethodType.TOSS, 18000L,
                        null)
        );
        Payment point = paymentRepository.save(
                Payment.createApproved("test_order_number2", "test_payment_key_random2", PaymentMethodType.POINT, 0L,
                        12000)
        );

        paymentHistoryRepository.save(PaymentHistory.approved(toss, 18000L, OffsetDateTime.now().minusDays(2)));
        paymentHistoryRepository.save(PaymentHistory.approved(point, 12000L, OffsetDateTime.now().minusDays(1)));

        mockMvc.perform(get("/admin/payments/histories")
                        .param("start", start.toLocalDate().toString())
                        .param("end", end.toLocalDate().toString())
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].orderNumber").exists())
                .andExpect(jsonPath("$.data.content[1].orderNumber").exists());
    }

    @Test
    void 결제수단_기간별_조회() throws Exception {
        OffsetDateTime start = OffsetDateTime.now().minusDays(7);
        OffsetDateTime end = OffsetDateTime.now().plusDays(1);

        Payment toss1 = paymentRepository.save(
                Payment.createApproved("test_order_number1", "test_payment_key_random1", PaymentMethodType.TOSS, 10000L,
                        null)
        );
        Payment toss2 = paymentRepository.save(
                Payment.createApproved("test_order_number2", "test_payment_key_random2", PaymentMethodType.TOSS, 20000L,
                        null)
        );
        Payment point = paymentRepository.save(
                Payment.createApproved("test_order_number3", "test_payment_key_random3", PaymentMethodType.POINT, 0L,
                        5000)
        );


        paymentHistoryRepository.save(PaymentHistory.approved(toss1, 10000L, OffsetDateTime.now().minusDays(5)));
        paymentHistoryRepository.save(PaymentHistory.approved(toss2, 20000L, OffsetDateTime.now().minusDays(2)));
        paymentHistoryRepository.save(PaymentHistory.approved(point, 5000L, OffsetDateTime.now().minusDays(3)));

        mockMvc.perform(get("/admin/payments/histories")
                        .param("paymentType", PaymentMethodType.POINT.name())
                        .param("start", start.toLocalDate().toString())
                        .param("end", end.toLocalDate().toString())
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.content[0].paymentType").value("POINT"));
    }
}
