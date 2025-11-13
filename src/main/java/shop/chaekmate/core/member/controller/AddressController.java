package shop.chaekmate.core.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shop.chaekmate.core.member.dto.request.CreateAddressRequest;
import shop.chaekmate.core.member.dto.response.AddressResponse;
import shop.chaekmate.core.member.service.AddressService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members/{memberId}/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<Void> create(@PathVariable Long memberId,
                                       @Valid @RequestBody CreateAddressRequest request) {
        addressService.createAddress(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<AddressResponse>> getAllAddresses(@PathVariable Long memberId) {
        return ResponseEntity.ok(addressService.getAllAddresses(memberId));
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponse> getAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        return ResponseEntity.ok(addressService.getAddress(memberId, addressId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long memberId, @PathVariable Long addressId) {
        addressService.deleteAddress(memberId, addressId);
        return ResponseEntity.noContent().build();
    }
}