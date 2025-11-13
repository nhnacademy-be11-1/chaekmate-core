package shop.chaekmate.core.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.controller.docs.MemberControllerDocs;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.response.AvailabilityResponse;
import shop.chaekmate.core.member.dto.response.MemberGradeResponse;
import shop.chaekmate.core.member.service.MemberService;

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
    public ResponseEntity<AvailabilityResponse> checkLoginId(@RequestParam("loginId") String loginId) {
        boolean exists = memberService.isDuplicateLoginId(loginId);
        return ResponseEntity.ok(new AvailabilityResponse(!exists));
    }

    @GetMapping("/{memberId}/grades")
    @Override
    public ResponseEntity<MemberGradeResponse> getMemberGrade(@PathVariable Long memberId) {
        return ResponseEntity.ok(memberService.getMemberGrade(memberId));
    }

    @DeleteMapping("/{memberId}")
    @Override
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

}
