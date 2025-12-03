package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.common.annotation.RequiredAdmin;
import shop.chaekmate.core.order.controller.docs.OrderControllerDocs;
import shop.chaekmate.core.order.dto.request.OrderSaveRequest;
import shop.chaekmate.core.order.dto.response.OrderSaveResponse;
import shop.chaekmate.core.order.service.OrderService;

@RestController
@RequiredArgsConstructor
public class OrderController implements OrderControllerDocs {

    private final OrderService orderService;

    @PostMapping("/orders/save")
    public ResponseEntity<OrderSaveResponse> saveOrder(
            @RequestHeader(value = "X-Member-Id", required = false) Long memberId,
            @Valid @RequestBody OrderSaveRequest request) {

        OrderSaveResponse response = orderService.createOrder(memberId, request);
        return ResponseEntity.ok(response);
    }

    @RequiredAdmin
    @PostMapping("/admin/ordered-books/{orderedBookId}/shipping")
    public ResponseEntity<Void> startShipping(@PathVariable Long orderedBookId) {
        orderService.applyOrderedBookShipping(orderedBookId);
        return ResponseEntity.ok().build();
    }
}

