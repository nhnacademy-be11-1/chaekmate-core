package shop.chaekmate.core.payment.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.provider.PaymentProvider;

@Component
@Slf4j
@RequiredArgsConstructor
public class TossPaymentProvider implements PaymentProvider {

    private final WebClient webClient;

    private final OrderRepository orderRepository;

    @Value("${toss.api.url}")
    private String tossBaseUrl;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Override
    public PaymentMethodType getType() {
        return PaymentMethodType.TOSS;
    }

    @Transactional
    @Override
    public PaymentApproveResponse approve(PaymentApproveRequest request) {
        log.info("[TOSS] 결제 승인 요청 - 주문번호: {}", request.orderNumber());
        Map<String, Object> body = new HashMap<>(Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderNumber(),
                "amount", request.amount()
        ));

        try {
            PaymentApproveResponse response = webClient.post()
                    .uri(tossBaseUrl + "/payments/confirm")
                    .header("Authorization", getAuthorization())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(PaymentApproveResponse.class)
                    .block();

            log.info("{} {} {} {} {}", response.orderNumber(), response.paymentKey(), response.approvedAt(),
                    response.totalAmount(), response.status());
            log.info("[TOSS] 결제 승인 완료 - 주문번호: {}", request.orderNumber());
            return response;

        } catch (WebClientResponseException e) {
            String errorMessage = parseErrorMessage(e);
            log.error("오류메시지 {}", e.getMessage());
            log.error("[TOSS] 결제 승인 실패 - 주문번호: {}, 사유: {}", request.orderNumber(), errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    @Transactional
    @Override
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        // TODO # 1. 주문 번호 존재 하는지 검증
        log.info("[TOSS] 결제 취소 요청 - 주문번호: {}", request.orderNumber());

        Map<String, Object> body = new HashMap<>(Map.of("cancelReason", request.cancelReason()));

        //null 전체 취소 != 부분 취소
        if (request.cancelAmount() != null) {
            body.put("cancelAmount", request.cancelAmount());
        }

        try {
            PaymentCancelResponse response = webClient.post()
                    .uri(tossBaseUrl + "/payments/" + request.paymentKey() + "/cancel")
                    .header("Authorization", getAuthorization())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(PaymentCancelResponse.class)
                    .block();

            log.info("[TOSS 결제 취소 성공] 주문번호={}, 취소금액={}, 상태={}",
                    response.orderNumber(), response.canceledAmount(), response.status());

            return response;
        } catch (WebClientResponseException e) {
            log.error("[TOSS] 결제 취소 실패 - 주문번호: {}, 응답: {}", request.orderNumber(), e.getResponseBodyAsString());
            throw new IllegalArgumentException("Toss 결제 취소 중 오류가 발생했습니다.");
        }
    }

    private String parseErrorMessage(WebClientResponseException e) {
        String errorMessage = "결제 요청 중 오류가 발생했습니다.";
        try {
            JsonNode json = new ObjectMapper().readTree(e.getResponseBodyAsString());
            if (json.has("error")) {
                JsonNode error = json.get("error");
                String code = error.has("code") ? error.get("code").asText() : "UNKNOWN";
                String message = error.has("message") ? error.get("message").asText() : "오류 메시지 없음";
                errorMessage = String.format("[%s] %s", code, message);
            }
        } catch (Exception ignored) {
            log.warn("[TOSS] 응답 메시지 파싱 실패: {}", e.getResponseBodyAsString());
        }
        return errorMessage;
    }

    private String getAuthorization() {
        return "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
    }
}
