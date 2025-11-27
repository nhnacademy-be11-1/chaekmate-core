package shop.chaekmate.core.payment.provider.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.ApiApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.exception.ExceedCancelAmountException;
import shop.chaekmate.core.payment.exception.NotFoundPaymentKeyException;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TossPaymentProvider implements PaymentProvider {

    private final WebClient webClient;
    private final PaymentRepository paymentRepository;
    private final PaymentHistoryRepository paymentHistoryRepository;

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
        log.info("[TOSS] 결제 승인 요청 - 주문번호={}, 결제 금액={}, 포인트사용={}", request.orderNumber(), request.amount(),
                request.pointUsed());

        Map<String, Object> body = new HashMap<>(Map.of(
                "paymentKey", request.paymentKey(),
                "orderId", request.orderNumber(),
                "amount", request.amount()
        ));

        try {
            ApiApproveResponse apiResponse = webClient.post()
                    .uri(tossBaseUrl + "/payments/confirm")
                    .header("Authorization", getAuthorization())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(ApiApproveResponse.class)
                    .block();

            Payment payment = Payment.createApproved(
                    Objects.requireNonNull(apiResponse).orderNumber(),
                    apiResponse.paymentKey(),
                    getType(),
                    request.amount(),
                    request.pointUsed()
            );

            paymentRepository.save(payment);
            LocalDateTime now = LocalDateTime.now();

            paymentHistoryRepository.save(
                    PaymentHistory.approved(payment, request.amount() + request.pointUsed(), now));

            return new PaymentApproveResponse(
                    request.orderNumber(),
                    request.amount(),
                    request.pointUsed(),
                    PaymentStatusType.APPROVED.name(),
                    now
            );

        } catch (WebClientResponseException e) {
            String errorMessage = parseErrorMessage(e);
            log.error("[TOSS] 결제 승인 실패 - 주문번호={}, 사유={}", request.orderNumber(), errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    @Transactional
    @Override
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
        log.info("[TOSS] 결제 취소 요청 - paymentKey={}, 취소금액={}, 취소포인트={}, 사유={}",
                request.paymentKey(), request.cancelAmount(), request.pointCancelAmount(), request.cancelReason());

        Payment payment = paymentRepository.findByOrderNumberAndPaymentKey(request.orderNumber(), request.paymentKey())
                .orElseThrow(NotFoundPaymentKeyException::new);

        long cashCancelAmount = request.cancelAmount();
        int pointCancelAmount = request.pointCancelAmount();

        log.info("[TOSS] 취소할 금액 분배 - cashCancel={}, pointCancel={}", cashCancelAmount, pointCancelAmount);
        LocalDateTime canceledAt = LocalDateTime.now();

        if(cashCancelAmount>0){
            // 현금만 취소 요청
            Map<String, Object> body = new HashMap<>(Map.of(
                    "cancelReason", request.cancelReason(),
                    "cancelAmount", cashCancelAmount
            ));

            JsonNode apiResponse;

            try {
                apiResponse = webClient.post()
                        .uri(tossBaseUrl + "/payments/" + request.paymentKey() + "/cancel")
                        .header("Authorization", getAuthorization())
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(body)
                        .retrieve()
                        .bodyToMono(JsonNode.class)
                        .block();

            } catch (WebClientResponseException e) {
                String errorMessage = parseErrorMessage(e);
                log.error("[TOSS] 결제 취소 실패 - paymentKey={}, 사유={}", request.paymentKey(), errorMessage);
                throw new IllegalStateException(errorMessage);
            }

            if (apiResponse == null) {
                throw new IllegalStateException("Toss 서버에서 빈 응답이 도착했습니다.");
            }

            String status = apiResponse.get("status").asText();
            JsonNode cancelNode = apiResponse.get("cancels").get(0);
            long tossCanceledAmount = cancelNode.get("cancelAmount").asLong();
            canceledAt = OffsetDateTime.parse(cancelNode.get("canceledAt").asText()).toLocalDateTime();

            log.info("[TOSS] TOSS 취소 성공 - paymentKey={}, pgCanceled={}, status={} canceledAt={}", payment.getPaymentKey(),
                    tossCanceledAmount, status, canceledAt);
        }

        payment.applyCancel(cashCancelAmount, pointCancelAmount);

        paymentHistoryRepository.save(
                (payment.getPaymentStatus() == PaymentStatusType.CANCELED)
                        ? PaymentHistory.canceled(payment, cashCancelAmount + pointCancelAmount, request.cancelReason(), canceledAt)
                        : PaymentHistory.partialCanceled(payment, cashCancelAmount + pointCancelAmount, request.cancelReason(), canceledAt)
        );

        return new PaymentCancelResponse(
                payment.getOrderNumber(),
                request.cancelReason(),
                cashCancelAmount,      // 요청된 현금 취소 금액 -> 포인트 적립 반대로 계산(차감)
                pointCancelAmount,     // 적립된 포인트 환급액
                canceledAt,
                request.canceledBooks()
        );
    }


    // api 오류 파싱
    private String parseErrorMessage(WebClientResponseException e) {
        String code = "UNKNOWN";
        String message = "결제 요청 중 오류가 발생했습니다.";
        try {
            JsonNode json = new ObjectMapper().readTree(e.getResponseBodyAsString());
            if (json.has("error")) {
                JsonNode error = json.get("error");
                code = error.has("code") ? error.get("code").asText() : code;
                message = error.has("message") ? error.get("message").asText() : message;
            }
        } catch (Exception ignored) {
            log.warn("[TOSS] 응답 메시지 파싱 실패: {}", e.getResponseBodyAsString());
        }
        return String.format("%s:%s", code.trim(), message.trim());
    }

    private String getAuthorization() {
        return "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
    }
}
