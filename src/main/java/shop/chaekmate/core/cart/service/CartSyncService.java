package shop.chaekmate.core.cart.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.repository.CartItemRepository;
import shop.chaekmate.core.cart.repository.CartRepository;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.exception.MemberNotFoundException;
import shop.chaekmate.core.member.repository.MemberRepository;

/**
 * 장바구니 DB 동기화 전담 서비스 클래스
 * <p>
 * Write-Through 패턴에서 DB 저장/수정/삭제를 담당함
 * 회원 전용 영구 저장소 역할
 */
@Service
@RequiredArgsConstructor
public class CartSyncService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    /**
     * 장바구니 아이템을 DB에 저장하거나 업데이트함
     * <ul>
     *     <li>Cart가 없으면 먼저 생성</li>
     *     <li>CartItem이 이미 있으면 수량 업데이트</li>
     *     <li>CartItem이 없으면 새로 생성</li>
     * </ul>
     *
     * @param memberId 회원 ID
     * @param bookId 도서 ID
     * @param quantity 수량
     */
    @Transactional
    public void saveOrUpdateCartItem(Long memberId, Long bookId, int quantity) {
        // 1. Cart 조회 또는 생성
        Cart cart = this.cartRepository.findByMemberId(memberId)
                .orElseGet(() -> this.createCart(memberId));

        // 2. Book 조회
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        // 3. CartItem 조회 또는 생성
        CartItem cartItem = this.cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseGet(() -> CartItem.create(cart, book));

        // 4. 수량 업데이트
        if (Objects.nonNull(cartItem.getId())) {
            // 이미 존재하는 경우
            cartItem.updateQuantity(quantity);
        } else {
            // 새로 생성하는 경우
            this.cartItemRepository.save(cartItem);
        }
    }

    /**
     * 특정 장바구니 아이템을 DB에서 삭제함 (Soft Delete)
     *
     * @param memberId 회원 ID
     * @param bookId 도서 ID
     */
    @Transactional
    public void deleteCartItem(Long memberId, Long bookId) {
        Cart cart = this.cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (Objects.nonNull(cart)) {
            this.cartItemRepository.deleteByCartIdAndBookId(cart.getId(), bookId);
        }
    }

    /**
     * 여러 장바구니 아이템을 DB에서 일괄 삭제함 (Soft Delete)
     *
     * @param memberId 회원 ID
     * @param bookIds 도서 ID 리스트
     */
    @Transactional
    public void deleteCartItems(Long memberId, List<Long> bookIds) {
        if (Objects.isNull(bookIds) || bookIds.isEmpty()) {
            return;
        }

        Cart cart = this.cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (Objects.nonNull(cart)) {
            this.cartItemRepository.deleteByCartIdAndBookIds(cart.getId(), bookIds);
        }
    }


    /**
     * 장바구니 내 모든 아이템을 DB에서 삭제함 (Soft Delete)
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void deleteAllCartItems(Long memberId) {
        Cart cart = this.cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (Objects.nonNull(cart)) {
            this.cartItemRepository.deleteAllByCartId(cart.getId());
        }
    }

    /**
     * DB에서 회원의 장바구니 아이템을 모두 조회함 (로그인 시 Redis 로딩용)
     *
     * @param memberId 회원 ID
     * @return Map<bookId, quantity>
     */
    @Transactional(readOnly = true)
    public Map<Long, Integer> loadCartItemsFromDb(Long memberId) {
        Cart cart = this.cartRepository.findByMemberId(memberId)
                .orElse(null);

        if (Objects.isNull(cart)) {
            return Map.of();
        }

        List<CartItem> cartItems = this.cartItemRepository.findAllByCartId(cart.getId());

        Map<Long, Integer> itemsMap = new HashMap<>();
        for (CartItem item : cartItems) {
            itemsMap.put(item.getBook().getId(), item.getQuantity());
        }

        return itemsMap;
    }

    /* =========================== 내부 메서드 =========================== */

    /**
     * 새로운 Cart를 생성하여 DB에 저장함
     *
     * @param memberId 회원 ID
     * @return 생성된 Cart 엔티티
     */
    private Cart createCart(Long memberId) {
        Member member = this.memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        Cart cart = Cart.create(member);

        return this.cartRepository.save(cart);
    }
}