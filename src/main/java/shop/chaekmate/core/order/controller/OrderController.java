package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.order.controller.docs.OrderControllerDocs;
import shop.chaekmate.core.order.dto.request.OrderSaveRequest;
import shop.chaekmate.core.order.dto.response.OrderSaveResponse;
import shop.chaekmate.core.order.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<OrderSaveResponse> saveOrder(
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @Valid @RequestBody OrderSaveRequest request) {

        OrderSaveResponse response = orderService.createOrder(memberId, request);
        return ResponseEntity.ok(response);
    }
}

