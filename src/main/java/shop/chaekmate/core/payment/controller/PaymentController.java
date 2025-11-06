package shop.chaekmate.core.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentReadyRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentReadyResponse;
import shop.chaekmate.core.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/ready")
    public ResponseEntity<PaymentReadyResponse> ready(@RequestBody PaymentReadyRequest request) {
        PaymentReadyResponse response = paymentService.ready(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve")
    public ResponseEntity<PaymentApproveResponse> approve(@RequestBody PaymentApproveRequest request) {
        PaymentApproveResponse response = paymentService.approve(request);
        return ResponseEntity.ok(response);
    }

}
