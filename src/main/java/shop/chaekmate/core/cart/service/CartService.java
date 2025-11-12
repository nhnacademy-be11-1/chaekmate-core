package shop.chaekmate.core.cart.service;

import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.adaptor.CartStore;
import shop.chaekmate.core.cart.dto.request.CartDto;
import shop.chaekmate.core.cart.dto.request.CartItemDto;
import shop.chaekmate.core.cart.dto.response.CartItemListResponse;
import shop.chaekmate.core.cart.dto.response.CartItemResponse;
import shop.chaekmate.core.cart.dto.response.CartItemSingleResponse;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.exception.cart.CartNotFoundException;
import shop.chaekmate.core.cart.exception.cart.MemberNotFoundException;
import shop.chaekmate.core.cart.model.CartItemSortCriteria;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CartService {

    private final MemberRepository memberRepository;
    private final CartStore cartStore;

    /* 장바구니 */
    // 장바구니 삭제
    @Transactional
    public void deleteCart(CartDto dto) {
        Cart cart = this.cartStore.findCartByMemberId(dto.memberId());
        if (Objects.nonNull(cart)) {
            this.cartStore.deleteCart(cart);
        }
    }

    /* 장바구니 아이템 */
    // 장바구니 아이템 생성
    @Transactional
    public CartItemSingleResponse addCartItem(CartItemDto dto) {
        Member member = this.memberRepository.findById(dto.memberId())
                .orElseThrow(MemberNotFoundException::new);

        // 장바구니 존재하지 않는 경우, 첫 장바구니 담기 요청으로 간주 --> 장바구니 생성으로 처리
        Cart cart = this.cartStore.findCartByMemberId(member.getId());
        if (Objects.isNull(cart)) {
            cart = this.cartStore.saveCart(Cart.create(member));
        }

        // 장바구니 아이템 생성
        CartItem item = this.cartStore.addItem(cart.getId(), dto.bookId(), dto.quantity());

        return new CartItemSingleResponse(
                member.getId(),
                cart.getId(),
                item.getBook().getId(),
                item.getQuantity()
        );
    }

    // 장바구니 아이템 수량 업데이트
    // 최종 수량 업데이트 한 번만 요청 가정
    @Transactional
    public CartItemSingleResponse updateCartItem(CartItemDto dto) {
        Member member = this.memberRepository.findById(dto.memberId())
                .orElseThrow(MemberNotFoundException::new);

        // 장바구니 존재하지 않는 경우, 예외로 간주 --> 예외로 처리
        Cart cart = this.cartStore.findCartByMemberId(member.getId());
        if (Objects.isNull(cart)) {
            throw new CartNotFoundException();
        }

        CartItem item = this.cartStore.addItem(cart.getId(), dto.bookId(), dto.quantity());

        return new CartItemSingleResponse(
                member.getId(),
                cart.getId(),
                item.getBook().getId(),
                item.getQuantity()
        );
    }

    // 장바구니 아이템 삭제 - 단일 아이템 삭제
    @Transactional
    public void deleteCartItem(CartItemDto dto) {
        Member member = this.memberRepository.findById(dto.memberId())
                .orElseThrow(MemberNotFoundException::new);

        Cart cart = this.cartStore.findCartByMemberId(member.getId());
        if (Objects.isNull(cart)) {
            throw new CartNotFoundException();
        }

        this.cartStore.removeItem(cart.getId(), dto.bookId());
    }

    // 장바구니 아이템 삭제 - 전체 아이템 삭제(장바구니 비우기)
    @Transactional
    public void deleteAllCartItem(CartDto dto) {
        Member member = this.memberRepository.findById(dto.memberId())
                .orElseThrow(MemberNotFoundException::new);

        Cart cart = this.cartStore.findCartByMemberId(member.getId());
        if (Objects.isNull(cart)) {
            throw new CartNotFoundException();
        }

        this.cartStore.removeAllItem(cart.getId());
    }

    // 장바구니 아이템 조회 - 정렬 기준: 기본(최근순)
    @Transactional(readOnly = true)
    public CartItemListResponse getCartItemList(CartDto dto) {
        Cart cart = this.cartStore.findCartByMemberId(dto.memberId());
        if (Objects.isNull(cart)) {
            // 장바구니 존재하지 않는 경우, 빈 리스트 반환 --> 화면 조회 가능
            return new CartItemListResponse(dto.memberId(), null, List.of());
        }

        List<CartItem> cartItemList = this.cartStore.findItemList(cart.getId(), CartItemSortCriteria.CREATED_DESC);

        List<CartItemResponse> itemResponseList = cartItemList.stream()
                .map(item -> new CartItemResponse(item.getBook().getId(), item.getQuantity()))
                .toList();

        return new CartItemListResponse(dto.memberId(), cart.getId(), itemResponseList);
    }

    // TODO: 장바구니 아이템 조회 - 정렬 기준 추가 (도서명 오름/내림차순, 오래된순)
}
