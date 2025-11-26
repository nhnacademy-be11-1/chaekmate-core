package shop.chaekmate.core.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.service.AdminGradeService;
import shop.chaekmate.core.member.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/grades")
public class AdminGradeController {
    private final AdminGradeService adminGradeService;
    private final MemberService memberService;

    @PutMapping("/{gradeId}")
    public ResponseEntity<Void> updateGrade(@PathVariable Long gradeId, @RequestBody UpdateGradeRequest request) {
        adminGradeService.updateGrade(gradeId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(memberService.getAllGrades());
    }

    @PostMapping
    public ResponseEntity<Void> createGrade(@RequestBody CreateGradeRequest request) {
        adminGradeService.createGrade(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{gradeId}")
    public ResponseEntity<Void> deleteGrade(@PathVariable Long gradeId) {
        adminGradeService.deleteGrade(gradeId);
        return ResponseEntity.noContent().build();
    }
}
