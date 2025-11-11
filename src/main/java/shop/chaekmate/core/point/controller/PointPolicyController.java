package shop.chaekmate.core.point.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.point.controller.docs.PointPolicyCotrollerDocs;
import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.DeletePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.response.CreatePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.PointPolicyResponse;
import shop.chaekmate.core.point.dto.response.UpdatePointPolicyResponse;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.service.PointService;

import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PointPolicyController implements PointPolicyCotrollerDocs {
    private final PointService pointService;

    //단일 정책 불러오기
    @GetMapping({"/point-policies/{type}","/admin/point-policies/{type}"})
    public ResponseEntity<PointPolicyResponse> getPolicy(@PathVariable("type") PointEarnedType type) {
        var dto = pointService.getPolicyByType(type);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/admin/point-policies")
    public ResponseEntity<List<PointPolicyResponse>> getAllPolicies() {
        List<PointPolicyResponse> response = pointService.getAllPolicies();
        return ResponseEntity.ok(response);
    }

    //정책 생성
    @PostMapping("/admin/point-policies")
    public ResponseEntity<CreatePointPolicyResponse> createPointPolicy(@Valid @RequestBody CreatePointPolicyRequest req) {
        var created = pointService.createPointPolicyRequest(req);
        return ResponseEntity.status(201).body(created);
    }

    //정책 수정
    @PutMapping("/admin/point-policies/{type}")
    public ResponseEntity<UpdatePointPolicyResponse> updatePointPolicy(@PathVariable("type") PointEarnedType type,
                                                              @Valid @RequestBody UpdatePointPolicyRequest req) {
        log.info("Point Policy updated." + type);
        var updated = pointService.updatePointPolicy(req);
        return ResponseEntity.ok(updated);
    }

    //정책 삭제
    @DeleteMapping("/admin/point-policies/{type}")
    public ResponseEntity<Void> delete(@PathVariable("type") PointEarnedType type) {
        log.info("Point Policy Deleted.");
        pointService.deletePointPolicyResponse(new DeletePointPolicyRequest(type));
        return ResponseEntity.noContent().build();
    }
}
