package shop.chaekmate.core.order.controller;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.common.annotation.RequiredAdmin;
import shop.chaekmate.core.order.controller.docs.OrderHistoryControllerDocs;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;
import shop.chaekmate.core.order.entity.type.OrderStatusType;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.service.OrderHistoryService;

@RestController
@RequiredArgsConstructor
public class OrderHistoryController implements OrderHistoryControllerDocs {

    private final OrderHistoryService orderHistoryService;

    @Override
    @GetMapping("/orders/history/member")
    public ResponseEntity<Page<OrderHistoryResponse>> getMemberOrderHistory(
            @RequestHeader("X-Member-Id") Long memberId,
            @ParameterObject Pageable pageable) {
        Page<OrderHistoryResponse> history = orderHistoryService.findMemberOrderHistory(memberId, pageable);
        return ResponseEntity.ok(history);
    }

    @Override
    @GetMapping("/orders/history/non-member")
    public ResponseEntity<Page<OrderHistoryResponse>> getNonMemberOrderHistory(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String ordererName,
            @RequestParam(required = false) String ordererPhone,
            @ParameterObject Pageable pageable) {
        Page<OrderHistoryResponse> history = orderHistoryService.findNonMemberOrderHistory(orderNumber, ordererName, ordererPhone, pageable);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderHistoryResponse> getOrderDetail(@PathVariable Long orderId) {
        OrderHistoryResponse response = orderHistoryService.findOrderDetail(orderId);
        return ResponseEntity.ok(response);
    }

    @RequiredAdmin
    @GetMapping("/admin/orders")
    public ResponseEntity<Page<OrderHistoryResponse>> getAllOrders(
            @RequestParam(required = false) OrderStatusType orderStatus,
            @RequestParam(required = false) OrderedBookStatusType unitStatus,
            Pageable pageable) {

        Page<OrderHistoryResponse> response = orderHistoryService.findAllOrderPage(orderStatus, unitStatus, pageable);
        return ResponseEntity.ok(response);
    }
}
