package shop.chaekmate.core.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointValueRequest;
import shop.chaekmate.core.point.dto.response.ReadPointPolicyResponse;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.service.PointService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {
    private final PointService pointService;

    @PatchMapping("/{type}/value")
    public ResponseEntity<ReadPointPolicyResponse> updateValue(@PathVariable PointEarnedType type, @Valid @RequestBody UpdatePointValueRequest req) {
        var updated = pointService.updatePointValueByType(type, req);
        return ResponseEntity.ok(ReadPointPolicyResponse.fromEntity(updated));
    }

    // Unified update endpoint: accept type + point in body
    @PatchMapping
    public ResponseEntity<ReadPointPolicyResponse> updateByBody(@Valid @RequestBody UpdatePointPolicyRequest req) {
        var updated = pointService.updatePointValueByType(req.pointEarnedType(), new shop.chaekmate.core.point.dto.request.UpdatePointValueRequest(req.point()));
        return ResponseEntity.ok(ReadPointPolicyResponse.fromEntity(updated));
    }
}
