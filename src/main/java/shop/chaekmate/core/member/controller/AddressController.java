package shop.chaekmate.core.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.response.AddressResponse;
import shop.chaekmate.core.member.service.MemberService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/addresses")
public class AddressController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable Long memberId,
                                       @Valid @RequestBody CreateAddressRequest request) {
        memberService.createAddress(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

//    @GetMapping
//    public ResponseEntity<List<AddressResponse>> list(@PathVariable Long memberId) {
//        return ResponseEntity.ok(memberService.getAddresses(memberId));
//    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Long memberId,
                                       @PathVariable Long addressId) {
        memberService.deleteAddress(addressId); // 소유자 검증 포함
        return ResponseEntity.noContent().build();
    }
}