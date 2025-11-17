package shop.chaekmate.core.cart.controller;

import jakarta.validation.Valid;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import shop.chaekmate.core.cart.dto.CartItemCreateDto;
import shop.chaekmate.core.cart.dto.CartItemDeleteAllDto;
import shop.chaekmate.core.cart.dto.CartItemDeleteDto;
import shop.chaekmate.core.cart.dto.CartItemReadDto;
import shop.chaekmate.core.cart.dto.CartItemUpdateDto;
import shop.chaekmate.core.cart.dto.request.CartItemCreateRequest;
import shop.chaekmate.core.cart.dto.request.CartItemUpdateRequest;
import shop.chaekmate.core.cart.dto.response.CartItemListAdvancedResponse;
import shop.chaekmate.core.cart.dto.response.CartItemListResponse;
import shop.chaekmate.core.cart.dto.response.CartItemUpdateResponse;
import shop.chaekmate.core.cart.service.CartService;

/**
 * 장바구니 관련 REST API를 제공하는 컨트롤러
 * 회원과 비회원(Guest)의 장바구니 처리 로직을 공통으로 지원함
 */

@RestController
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 장바구니 담기
     * - 장바구니에 아이템을 추가함
     *
     * <p>회원의 경우 X-Member-Id 헤더를 통해 memberId를,
     * 비회원의 경우 Guest-Id 쿠키를 통해 guestId를 매핑하여 장바구니를 식별함</p>
     *
     * @param request   추가할 도서 ID 정보를 포함한 요청 객체
     * @param memberId  회원 식별자(헤더), 없으면 비회원으로 간주
     * @param guestId   비회원 식별자(쿠키), 회원일 경우 무시됨
     * @return          장바구니에 추가된 전체 아이템 리스트 응답
     */
    @PostMapping("/carts/items")
    public ResponseEntity<CartItemListResponse> addCartItem(
            @Valid @RequestBody CartItemCreateRequest request,
            @RequestHeader(value = "X-Member-Id", required = false) String memberId,
            @CookieValue(name = "Guest-Id", required = false) String guestId
    )
    {
        Long resolvedMemberId = (Objects.nonNull(memberId)) ? Long.valueOf(memberId) : null;
        String resolvedGuestId = (Objects.isNull(memberId)) ? guestId : null;

        CartItemCreateDto dto = new CartItemCreateDto(
                resolvedMemberId,
                resolvedGuestId,
                request.bookId()
        );

        CartItemListResponse response = this.cartService.addCartItem(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 장바구니 조회
    /**
     * 장바구니 조회
     * - 장바구니 아이템 전체를 조회함
     *
     * <p>회원/비회원 여부에 따라 다른 저장소를 조회하며,
     * 도서 상세 정보를 포함한 확장된 응답을 반환함</p>
     *
     * @param memberId  회원 ID(헤더)
     * @param guestId   비회원 Guest ID(쿠키)
     * @return          장바구니 아이템 목록 + 도서 정보가 포함된 응답
     */
    @GetMapping("/carts")
    public ResponseEntity<CartItemListAdvancedResponse> getCart(
            @RequestHeader(value = "X-Member-Id", required = false) String memberId,
            @CookieValue(name = "Guest-Id", required = false) String guestId
    )
    {
        Long resolvedMemberId = (Objects.nonNull(memberId)) ? Long.valueOf(memberId) : null;
        String resolvedGuestId = (Objects.isNull(memberId)) ? guestId : null;

        CartItemReadDto dto = new CartItemReadDto(
                resolvedMemberId,
                resolvedGuestId
        );

        CartItemListAdvancedResponse response = this.cartService.getCartItemsWithBookInfo(dto);

        return ResponseEntity.ok(response);
    }

    // 장바구니 아이템 수량 변경
    /**
     * 장바구니 아이템 수량 변경
     * - 장바구니 아이템의 수량을 변경함
     *
     * @param bookId    수량을 변경할 도서 ID
     * @param request   변경할 수량 정보 요청 객체
     * @param memberId  회원 식별자(헤더)
     * @param guestId   비회원 식별자(쿠키)
     * @return          변경된 장바구니 아이템 정보 응답
     */
    @PutMapping("/carts/items/{bookId}")
    public ResponseEntity<CartItemUpdateResponse> updateCartItem(
            @PathVariable Long bookId,
            @RequestBody CartItemUpdateRequest request,
            @RequestHeader(value = "X-Member-Id", required = false) String memberId,
            @CookieValue(name = "Guest-Id", required = false) String guestId
    )
    {
        Long resolvedMemberId = (Objects.nonNull(memberId)) ? Long.valueOf(memberId) : null;
        String resolvedGuestId = (Objects.isNull(memberId)) ? guestId : null;

        CartItemUpdateDto dto = new CartItemUpdateDto(
                resolvedMemberId,
                resolvedGuestId,
                bookId,
                request.quantity()
        );

        CartItemUpdateResponse response = this.cartService.updateCartItem(dto);

        return ResponseEntity.ok(response);
    }

    // 장바구니 아이템 삭제
    /**
     * 장바구니 아이템 단일 삭제
     * - 장바구니에서 특정 아이템(도서 ID 기준)을 삭제함
     *
     * <p>회원 또는 비회원 장바구니 각각의 저장소에서 제거됨</p>
     *
     * @param bookId    삭제할 도서 ID
     * @param memberId  회원 ID(헤더)
     * @param guestId   비회원 Guest ID(쿠키)
     * @return          HTTP 204 No Content
     */
    @DeleteMapping("/carts/items/{bookId}")
    public ResponseEntity<Void> deleteCartItem(
            @PathVariable Long bookId,
            @RequestHeader(value = "X-Member-Id", required = false) String memberId,
            @CookieValue(name = "Guest-Id", required = false) String guestId
    )
    {
        Long resolvedMemberId = (Objects.nonNull(memberId)) ? Long.valueOf(memberId) : null;
        String resolvedGuestId = (Objects.isNull(memberId)) ? guestId : null;

        CartItemDeleteDto dto = new CartItemDeleteDto(
                resolvedMemberId,
                resolvedGuestId,
                bookId
        );

        this.cartService.deleteCartItem(dto);

        return ResponseEntity.noContent().build();
    }

    // 장바구니 비우기
    /**
     * 장바구니 비우기
     * - 장바구니의 모든 아이템을 삭제(비우기)함
     *
     * <p>로그인/비로그인 사용자의 장바구니를 모두 지원함</p>
     *
     * @param memberId  회원 ID(헤더)
     * @param guestId   비회원 Guest ID(쿠키)
     * @return          HTTP 204 No Content
     */
    @DeleteMapping("/carts/items")
    public ResponseEntity<Void> flushCart(
            @RequestHeader(value = "X-Member-Id", required = false) String memberId,
            @CookieValue(name = "Guest-Id", required = false) String guestId
    )
    {
        Long resolvedMemberId = (Objects.nonNull(memberId)) ? Long.valueOf(memberId) : null;
        String resolvedGuestId = (Objects.isNull(memberId)) ? guestId : null;

        CartItemDeleteAllDto dto = new CartItemDeleteAllDto(
                resolvedMemberId,
                resolvedGuestId
        );

        this.cartService.deleteAllCartItems(dto);

        return ResponseEntity.noContent().build();
    }

}
