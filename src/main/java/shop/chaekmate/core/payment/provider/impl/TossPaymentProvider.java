package shop.chaekmate.core.payment.provider.impl;

import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import shop.chaekmate.core.order.repository.OrderRepository;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.request.PaymentReadyRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.dto.response.PaymentReadyResponse;
import shop.chaekmate.core.payment.entity.type.PaymentMethod;
import shop.chaekmate.core.payment.provider.PaymentProvider;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class TossPaymentProvider implements PaymentProvider {

    private final WebClient webClient;
    private final OrderRepository orderRepository;

    @Value("${toss.api.url}")
    private String tossBaseUrl;

    @Value("${toss.secret-key}")
    private String tossSecretKey;

    @Value("${toss.success-url}")
    private String successUrl;

    @Value("${toss.fail-url}")
    private String failUrl;

    private String getAuthorization() {
        return "Basic " + Base64.getEncoder().encodeToString((tossSecretKey + ":").getBytes());
    }

    @Override
    public PaymentReadyResponse ready(PaymentReadyRequest request) {
//        log.info("[TOSS] 결제 준비 요청 - 주문번호: {}", request.orderNumber());
//
//        Order order = orderRepository.findByOrderNumber(request.orderNumber())
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주문입니다."));
//
//        if (order.isPaid()) {
//            throw new IllegalArgumentException("이미 결제 완료된 주문입니다.");
//        }
//
//        Map<String, Object> body = Map.of(
//                "orderId", request.orderNumber(),
//                "amount", request.amount(),
//                "orderName", "Chaekmate 주문 결제",
//                "successUrl", successUrl,
//                "failUrl", failUrl
//        );
//
//        try {
//            PaymentReadyResponse response = webClient.post()
//                    .uri(tossBaseUrl + "/payments")
//                    .header("Authorization", getAuthorization())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(body)
//                    .retrieve()
//                    .bodyToMono(PaymentReadyResponse.class)
//                    .block();
//
//            order.changeStatus(OrderStatus.PAYMENT_READY);
//
//            log.info("[TOSS] 결제 준비 완료 - 주문번호: {}, redirectUrl={}", request.orderNumber(),
//                    response != null ? response.toString() : "N/A");
//
//            return response;
//        } catch (WebClientResponseException e) {
//            log.error("[TOSS] 결제 준비 실패 - 주문번호: {}, 응답: {}", request.orderNumber(), e.getResponseBodyAsString());
//            throw new IllegalArgumentException("Toss 결제 준비 중 오류가 발생했습니다.");
//        }
        return null;
    }

    @Override
    public PaymentApproveResponse approve(PaymentApproveRequest request) {
//        log.info("[TOSS] 결제 승인 요청 - 주문번호: {}", request.orderNumber());
//
//        Map<String, Object> body = Map.of(
//                "paymentKey", request.paymentKey(),
//                "orderId", request.orderNumber(),
//                "amount", request.amount()
//        );
//
//        try {
//            PaymentApproveResponse response = webClient.post()
//                    .uri(tossBaseUrl + "/payments/confirm")
//                    .header("Authorization", getAuthorization())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(body)
//                    .retrieve()
//                    .bodyToMono(PaymentApproveResponse.class)
//                    .block();
//
//            orderRepository.findByOrderNumber(request.orderNumber())
//                    .ifPresent(Order::markAsPaid);
//
//            log.info("[TOSS] 결제 승인 완료 - 주문번호: {}", request.orderNumber());
//            return response;
//        } catch (WebClientResponseException e) {
//            log.error("[TOSS] 결제 승인 실패 - 주문번호: {}, 응답: {}", request.orderNumber(), e.getResponseBodyAsString());
//            throw new IllegalArgumentException("Toss 결제 승인 중 오류가 발생했습니다.");
//        }
        return null;
    }

    @Override
    public PaymentCancelResponse cancel(PaymentCancelRequest request) {
//        log.info("[TOSS] 결제 취소 요청 - 주문번호: {}", request.orderNumber());
//
//        Map<String, Object> body = Map.of("cancelReason", request.cancelReason());
//
//        try {
//            PaymentCancelResponse response = webClient.post()
//                    .uri(tossBaseUrl + "/payments/" + request.paymentKey() + "/cancel")
//                    .header("Authorization", getAuthorization())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(body)
//                    .retrieve()
//                    .bodyToMono(PaymentCancelResponse.class)
//                    .block();
//
//            orderRepository.findByOrderNumber(request.orderNumber())
//                    .ifPresent(Order::cancel);
//
//            log.info("[TOSS] 결제 취소 완료 - 주문번호: {}", request.orderNumber());
//            return response;
//        } catch (WebClientResponseException e) {
//            log.error("[TOSS] 결제 취소 실패 - 주문번호: {}, 응답: {}", request.orderNumber(), e.getResponseBodyAsString());
//            throw new IllegalArgumentException("Toss 결제 취소 중 오류가 발생했습니다.");
//        }
        return null;
    }

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.TOSS;
    }
}
