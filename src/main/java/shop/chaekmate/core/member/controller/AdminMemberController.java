package shop.chaekmate.core.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.controller.docs.AdminMemberControllerDocs;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.service.AdminMemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/members")
public class AdminMemberController implements AdminMemberControllerDocs{
    private final AdminMemberService adminMemberService;

    @Override
    @GetMapping
    public ResponseEntity<List<MemberResponse>> getMembers(
            @RequestParam(defaultValue = "ACTIVE") String status
    ) {
        if ("DELETED".equalsIgnoreCase(status)) {
            return ResponseEntity.ok(adminMemberService.getDeletedMembers());
        }
        return ResponseEntity.ok(adminMemberService.getActiveMembers());
    }

    @Override
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMemberById(@PathVariable("memberId") Long memberId) {
        adminMemberService.deleteMember(memberId);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/{memberId}/restore")
    public ResponseEntity<Void> restoreMemberById(@PathVariable("memberId") Long memberId) {
        adminMemberService.restoreMember(memberId);
        return ResponseEntity.ok().build();
    }
}
