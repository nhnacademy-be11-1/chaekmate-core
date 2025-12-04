package shop.chaekmate.core.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.common.annotation.RequiredAdmin;
import shop.chaekmate.core.order.dto.request.ReturnBooksRequest;
import shop.chaekmate.core.order.dto.response.ReturnBooksResponse;
import shop.chaekmate.core.payment.controller.docs.PaymentControllerDocs;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.base.PaymentResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.service.PaymentService;

@Controller
@RequiredArgsConstructor
public class PaymentController implements PaymentControllerDocs {

    private final PaymentService paymentService;

    @Override
    @PostMapping("/payments/approve")
    public ResponseEntity<PaymentResponse> approve(@RequestHeader(value = "X-Member-Id", required = false) Long memberId,
                                                   @Valid @RequestBody PaymentApproveRequest request) {
        PaymentResponse response = paymentService.approve(memberId, request);
        return ResponseEntity.ok(response);
    }

    @Override
    @PostMapping("/payments/cancel")
    public ResponseEntity<PaymentCancelResponse> cancel(@RequestHeader(value = "X-Member-Id", required = false) Long memberId,
                                                        @Valid @RequestBody PaymentCancelRequest request) {
        PaymentCancelResponse response = paymentService.cancel(memberId, request);
        return ResponseEntity.ok(response);
    }
    @Override
    @PostMapping("/payments/return-request")
    public ResponseEntity<ReturnBooksResponse> requestReturn(@RequestHeader(value = "X-Member-Id", required = false) Long memberId,
                                                             @Valid @RequestBody ReturnBooksRequest request) {
        ReturnBooksResponse response = paymentService.requestReturn(memberId, request);
        return ResponseEntity.ok(response);
    }

    @RequiredAdmin
    @Override
    @PostMapping("/admin/payments/return-approve")
    public ResponseEntity<ReturnBooksResponse> returnApprove(@Valid @RequestBody ReturnBooksRequest request) {
        ReturnBooksResponse response = paymentService.approveReturn(request);
        return ResponseEntity.ok(response);
    }
}
