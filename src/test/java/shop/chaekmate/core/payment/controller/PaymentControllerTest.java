package shop.chaekmate.core.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.type.PaymentType;
import shop.chaekmate.core.payment.provider.impl.TossPaymentProvider;
import shop.chaekmate.core.payment.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentControllerTest {

    @Autowired
    TossPaymentProvider tossPaymentProvider;

    @Test
    void toss_결제승인_API_호출() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentType.TOSS,
                "test_sk_GjLJoQ1aVZbyBBQ2EYKPVw6KYe2R",
                "ORDER-20251104-001",
                29800
        );

        PaymentApproveResponse response = tossPaymentProvider.approve(request);
        assertEquals("DONE", response.status());
    }

//    @DisplayName("결제 승인 요청 성공 시 200 OK와 응답 본문을 반환한다.")
//    @Test
//    void approvePayment_Success() throws Exception {
//        // given
//        PaymentApproveRequest request = new PaymentApproveRequest(
//                PaymentType.TOSS,
//                "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm",
//                "ORDER-20251104-001",
//                29800
//        );
//
//        PaymentApproveResponse response = new PaymentApproveResponse(
//                "ORDER-20251104-001",
//                PaymentType.TOSS.name(),
//                29800L,
//                "DONE",
//                LocalDateTime.of(2025,11,11,11,11,11)
//        );
//
//
//        // when & then
//        mockMvc.perform(post("/api/payments/approve")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.orderNumber").value("ORDER-20251104-001"))
//                .andExpect(jsonPath("$.paymentMethod").value("TOSS"))
//                .andExpect(jsonPath("$.approvedAmount").value(29800))
//                .andExpect(jsonPath("$.status").value("DONE"))
//                .andExpect(jsonPath("$.approvedAt").value(LocalDateTime.of(2025,11,11,11,11,11)
//                ));
//    }

//    @DisplayName("결제 승인 요청 실패 시 400 에러를 반환한다.")
//    @Test
//    void approvePayment_Failure() throws Exception {
//        // given
//        PaymentApproveRequest request = new PaymentApproveRequest(
//                "invalid_payment_key",
//                "ORDER-20251104-999",
//                29800,
//                PaymentType.TOSS
//        );
//
//        Mockito.when(paymentService.approve(any()))
//                .thenThrow(new IllegalArgumentException("Toss 결제 승인 중 오류가 발생했습니다."));
//
//        // when & then
//        mockMvc.perform(post("/api/payments/approve")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.message").value("Toss 결제 승인 중 오류가 발생했습니다."));
//    }
}
