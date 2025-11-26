package shop.chaekmate.core.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.common.annotation.RequiredAdmin;
import shop.chaekmate.core.order.controller.docs.DeliveryControllerDocs;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyDto;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyRequest;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyHistoryResponse;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyResponse;
import shop.chaekmate.core.order.service.DeliveryPolicyService;

@RestController
@RequiredArgsConstructor
public class DeliveryPolicyController implements DeliveryControllerDocs {

    private final DeliveryPolicyService deliveryPolicyService;

    @RequiredAdmin
    @PostMapping("/admin/delivery-policy")
    public ResponseEntity<DeliveryPolicyResponse> createDeliveryPolicy(
            @Valid @RequestBody DeliveryPolicyRequest request) {
        DeliveryPolicyDto dto = new DeliveryPolicyDto(request.freeStandardAmount(), request.deliveryFee());
        DeliveryPolicyResponse response = deliveryPolicyService.createPolicy(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @RequiredAdmin
    @GetMapping("/admin/delivery-policy")
    public ResponseEntity<Page<DeliveryPolicyHistoryResponse>> getDeliveryPolicies(
            @PageableDefault(size = 15) Pageable pageable) {
        Page<DeliveryPolicyHistoryResponse> response = deliveryPolicyService.getPolicyHistory(pageable);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/delivery-policy")
    public ResponseEntity<DeliveryPolicyResponse> getCurrentDeliveryPolicy() {
        DeliveryPolicyResponse response = deliveryPolicyService.getPolicy();
        return ResponseEntity.ok().body(response);
    }
}
