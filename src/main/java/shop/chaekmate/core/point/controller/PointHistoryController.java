package shop.chaekmate.core.point.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.point.dto.response.MemberPointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointResponse;
import shop.chaekmate.core.point.service.PointHistoryService;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PointHistoryController {
    private final PointHistoryService pointHistoryService;

    // 모든 history 불러오기
    @GetMapping(path = "/admin/point-histories")
    public ResponseEntity<Page<PointHistoryResponse>> getPointHistory(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PointHistoryResponse> response = pointHistoryService.getPointHistory(pageable);
        return ResponseEntity.ok().body(response);
    }

    // 특정 회원의 포인트 history 조회
    @GetMapping(path = "/members/{memberId}/point-histories")
    public ResponseEntity<Page<MemberPointHistoryResponse>> getMemberPointHistory(
            @PathVariable("memberId") Long memberId,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        log.info("회원 {} 포인트 히스토리 조회", memberId);
        Page<MemberPointHistoryResponse> response = pointHistoryService.getPointHistoryByMemberId(memberId, pageable);
        return ResponseEntity.ok().body(response);
    }

    // 특정 회원의 포인트 잔액 조회
    @GetMapping(path = "/members/{memberId}/points")
    public ResponseEntity<PointResponse> getMemberPoint(
            @PathVariable("memberId") Long memberId) {
        log.info("회원 {} 포인트 잔액 조회", memberId);
        PointResponse response = pointHistoryService.readPointResponse(memberId);
        return ResponseEntity.ok().body(response);
    }
}