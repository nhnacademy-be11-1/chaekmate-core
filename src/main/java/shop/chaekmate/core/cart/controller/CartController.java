package shop.chaekmate.core.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.cart.dto.request.CartDto;
import shop.chaekmate.core.cart.dto.request.CartItemDto;
import shop.chaekmate.core.cart.dto.request.CartItemRequest;
import shop.chaekmate.core.cart.dto.request.CartRequest;
import shop.chaekmate.core.cart.dto.response.CartItemListResponse;
import shop.chaekmate.core.cart.dto.response.CartItemSingleResponse;
import shop.chaekmate.core.cart.service.CartService;

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /* --------------------------- 장바구니 아이템 --------------------------- */
    // 장바구니 담기
    @PostMapping("/carts/items")
    public ResponseEntity<CartItemSingleResponse> createCartItem(@Valid @RequestBody CartItemRequest request) {
        CartItemDto dto = new CartItemDto(request.memberId(), request.cartId(), request.cartItemId(), request.bookId(), request.quantity());
        CartItemSingleResponse response = this.cartService.addCartItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 수량 변경
    @PutMapping("/carts/items/{cartItemId}")
    public ResponseEntity<CartItemSingleResponse> updateCartItem(@PathVariable Long cartItemId,
                                                                 @Valid @RequestBody CartItemRequest request) {
        CartItemDto dto = new CartItemDto(request.memberId(), request.cartId(), cartItemId, request.bookId(), request.quantity());
        CartItemSingleResponse response = this.cartService.updateCartItem(dto);
        return ResponseEntity.ok(response);
    }

    // 장바구니 아이템 단일 삭제
    @DeleteMapping("/carts/items/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId,
                                               @Valid @RequestBody CartItemRequest request) {
        CartItemDto dto = new CartItemDto(request.memberId(), request.cartId(), cartItemId, request.bookId(), request.quantity());
        this.cartService.deleteCartItem(dto);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 아이템 전체 삭제 (장바구니 비우기)
    @DeleteMapping("/carts/{cartId}")
    public ResponseEntity<Void> deleteAllCartItems(@Valid @RequestBody CartRequest request) {
        CartDto dto = new CartDto(request.memberId());
        this.cartService.deleteAllCartItem(dto);
        return ResponseEntity.noContent().build();
    }

    // 장바구니 목록 조회
    @GetMapping("/carts")
    public ResponseEntity<CartItemListResponse> getAllCartItems(@Valid @RequestBody CartRequest request) {
        CartDto dto = new CartDto(request.memberId());
        CartItemListResponse response = this.cartService.getCartItemList(dto);
        return ResponseEntity.ok(response);
    }

}
