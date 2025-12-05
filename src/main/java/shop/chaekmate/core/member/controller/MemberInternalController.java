package shop.chaekmate.core.member.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.member.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MemberInternalController {

    private final MemberService memberService;

    @GetMapping("/api/internal/members/birth-month")
    public List<Long> getMemberIdsByBirthMonth(
            @RequestParam int month) {
        return memberService.getMemberIdsByBirthMonth(month);
    }
}
