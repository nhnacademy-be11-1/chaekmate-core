package shop.chaekmate.core.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.controller.docs.MemberControllerDocs;
import shop.chaekmate.core.member.dto.request.CreateMemberGradeHistoryRequest;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.AvailabilityResponse;
import shop.chaekmate.core.member.dto.response.GradeResponse;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping
    @Override
    public ResponseEntity<Void> createMember(@Valid @RequestBody CreateMemberRequest request) {
        memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/check-login-id")
    @Override
    public ResponseEntity<AvailabilityResponse> checkLoginId(@RequestParam("loginId") String loginId) {
        boolean exists = memberService.isDuplicateLoginId(loginId);
        return ResponseEntity.ok(new AvailabilityResponse(!exists));
    }

    @GetMapping("/check-email")
    @Override
    public ResponseEntity<AvailabilityResponse> checkEmail(@RequestParam("email") String email) {
        boolean exists = memberService.isDuplicateEmail(email);
        return ResponseEntity.ok(new AvailabilityResponse(!exists));
    }

    @PostMapping("/{memberId}")
    public ResponseEntity<MemberResponse> updateMEmber(@PathVariable Long memberId, UpdateMemberRequest request) {
        memberService.updateMember(memberId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}")
    @Override
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{memberId}")
    @Override
    public ResponseEntity<MemberResponse> getMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMember(memberId));
    }

    @GetMapping("/{memberId}/grade")
    @Override
    public ResponseEntity<GradeResponse> getMemberGrade(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberGrade(memberId));
    }

    @GetMapping("/grades")
    @Override
    public ResponseEntity<List<GradeResponse>> getAllGrades() {
        return ResponseEntity.ok(memberService.getAllGrades());
    }

    @PostMapping("/grades")
    @Override
    public ResponseEntity<Void> createMemberGradeHistory(@RequestBody CreateMemberGradeHistoryRequest request) {
        memberService.createMemberGradeHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
