package shop.chaekmate.core.point.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.point.dto.response.PointHistoryResponse;
import shop.chaekmate.core.point.service.PointHistoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    // 모든 history 불러오기
    @GetMapping(path = "/admin/point-histories")
    public ResponseEntity<Page<PointHistoryResponse>> getPointHistory(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PointHistoryResponse> response = pointHistoryService.getPointHistory(pageable);
        return ResponseEntity.ok().body(response);
    }
}