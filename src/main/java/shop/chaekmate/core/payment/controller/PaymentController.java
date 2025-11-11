package shop.chaekmate.core.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.payment.controller.docs.PaymentControllerDocs;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.service.PaymentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    @PostMapping("/approve")
    @Override
    public ResponseEntity<PaymentApproveResponse> approve(@RequestBody PaymentApproveRequest request) {
        PaymentApproveResponse response = paymentService.approve(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cancel")
    @Override
    public ResponseEntity<PaymentCancelResponse> cancel(@RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = paymentService.cancel(request);
        return ResponseEntity.ok(response);
    }
}
