package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.order.controller.docs.OrderHistoryControllerDocs;
import shop.chaekmate.core.order.dto.request.NonMemberOrderHistoryRequest;
import shop.chaekmate.core.order.dto.response.OrderHistoryResponse;
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
            @Valid @RequestBody NonMemberOrderHistoryRequest request,
            @ParameterObject Pageable pageable) {
        Page<OrderHistoryResponse> history = orderHistoryService.findNonMemberOrderHistory(request, pageable);
        return ResponseEntity.ok(history);
    }
}
