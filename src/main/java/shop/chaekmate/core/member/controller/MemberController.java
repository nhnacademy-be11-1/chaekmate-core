package shop.chaekmate.core.member.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.controller.docs.MemberControllerDocs;
import shop.chaekmate.core.member.dto.request.CreateMemberRequest;
import shop.chaekmate.core.member.dto.request.UpdateMemberRequest;
import shop.chaekmate.core.member.dto.response.MemberResponse;
import shop.chaekmate.core.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController implements MemberControllerDocs {

    private final MemberService memberService;

    @PostMapping
    @Override
    public ResponseEntity<MemberResponse> createMember(@Valid @RequestBody CreateMemberRequest request) {
        MemberResponse response = memberService.createMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<MemberResponse> readMember(@PathVariable Long id) {
        MemberResponse response = memberService.readMember(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Override
    public ResponseEntity<List<MemberResponse>> readAllMembers() {
        List<MemberResponse> responses = memberService.readAllMembers();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<MemberResponse> updateMember(@PathVariable Long id,
                                                       @Valid @RequestBody UpdateMemberRequest request) {
        MemberResponse response = memberService.updateMember(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }
}
