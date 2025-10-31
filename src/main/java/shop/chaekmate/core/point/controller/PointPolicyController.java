package shop.chaekmate.core.point.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.DeletePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.response.CreatePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.UpdatePointPolicyResponse;
import shop.chaekmate.core.point.service.PointService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {
    private final PointService pointService;

    @PostMapping
    public ResponseEntity<CreatePointPolicyResponse> create(@Valid @RequestBody CreatePointPolicyRequest req) {
        var created = pointService.createPointPolicyRequest(req);
        return ResponseEntity.status(201).body(created);
    }

    @PatchMapping
    public ResponseEntity<UpdatePointPolicyResponse> update(@Valid @RequestBody UpdatePointPolicyRequest req) {
        var updated = pointService.updatePointPolicy(req);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@Valid @RequestBody DeletePointPolicyRequest req) {
        pointService.deletePointPolicyResponse(req);
        return ResponseEntity.noContent().build();
    }
}
