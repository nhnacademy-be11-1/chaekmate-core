package shop.chaekmate.core.cart.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.repository.BookImageRepository;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.dto.CartItemCreateDto;
import shop.chaekmate.core.cart.dto.CartItemDeleteAllDto;
import shop.chaekmate.core.cart.dto.CartItemDeleteDto;
import shop.chaekmate.core.cart.dto.CartItemUpdateDto;
import shop.chaekmate.core.cart.dto.CartOwner;
import shop.chaekmate.core.cart.dto.response.CartItemAdvancedResponse;
import shop.chaekmate.core.cart.dto.response.CartItemListAdvancedResponse;
import shop.chaekmate.core.cart.dto.response.CartItemListResponse;
import shop.chaekmate.core.cart.dto.response.CartItemResponse;
import shop.chaekmate.core.cart.dto.response.CartItemUpdateResponse;
import shop.chaekmate.core.cart.dto.response.CartItemCountResponse;
import shop.chaekmate.core.cart.exception.BookInsufficientStockException;
import shop.chaekmate.core.cart.exception.CartItemNotFoundException;
import shop.chaekmate.core.cart.exception.CartNotFoundException;
import shop.chaekmate.core.cart.repository.CartRedisRepository;

/**
 * 장바구니 관련 비즈니스 로직을 처리하는 서비스 클래스
 * <p>
 * Write-Through 패턴 적용:
 * - 회원: 모든 쓰기 작업은 Redis + DB 동시 반영
 * - 비회원: Redis만 사용 (DB 저장X)
 */
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final CartSyncService cartSyncService;
    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;

    /**
     * 장바구니 아이템을 추가함 (Write-Through)
     * <ul>
     *     <li>회원: 1. Redis에 저장 → 2. DB에 동시 저장 (백업)</li>
     *     <li>비회원: Redis만 저장</li>
     * </ul>
     */
    @Transactional
    public CartItemListResponse addCartItem(CartItemCreateDto dto) {
        // 장바구니 조회 또는 생성
        String cartId = this.resolveOrCreateCartId(dto);

        // 도서 재고 검증
        var book = this.bookRepository.findById(dto.bookId())
                .orElseThrow(BookNotFoundException::new);

        int stock = book.getStock();
        if (dto.quantity() > stock) {
            throw new BookInsufficientStockException();
        }

        // 1. Redis 저장 (회원/비회원, 우선 저장)
        this.cartRedisRepository.putCartItem(cartId, dto.bookId(), dto.quantity());

        // 2. DB 저장 (회원, Write-Through)
        if (Objects.nonNull(dto.memberId())) {
            this.cartSyncService.saveOrUpdateCartItem(dto.memberId(), dto.bookId(), dto.quantity());
        }

        // 장바구니 전체 조회 후 Response DTO 반환
        Map<Long, Integer> itemsMap = this.cartRedisRepository.getAllCartItems(cartId);

        List<CartItemResponse> items = itemsMap.entrySet().stream()
                .map(e -> new CartItemResponse(e.getKey(), e.getValue()))
                .toList();

        return new CartItemListResponse(Long.parseLong(cartId), items);
    }

    /**
     * 장바구니 아이템 목록을 조회하고 도서 정보 및 대표 이미지까지 포함하여 반환함
     * <ul>
     *     <li>Redis 조회</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public CartItemListAdvancedResponse getCartItemsWithBookInfo(CartOwner dto) {
        // 장바구니 조회 또는 생성
        String cartId = this.resolveOrCreateCartId(dto);

        // Redis에서 장바구니 아이템 조회
        Map<Long, Integer> cartItems = this.cartRedisRepository.getAllCartItems(cartId);

        // 각 책 정보 + 대표 이미지 조회 후 DTO 변환
        List<CartItemAdvancedResponse> items = cartItems.entrySet().stream().map(entry -> {
            Long bookId = entry.getKey();
            int quantity = entry.getValue();

            // 책 정보 조회
            var book = this.bookRepository.findById(bookId)
                    .orElseThrow(BookNotFoundException::new);

            // 책 이미지 조회 (대표 이미지 선택)
            var images = this.bookImageRepository.findAllByBookIdOrderByCreatedAtAsc(bookId);
            String bookImageUrl = images.isEmpty() ? null : images.getFirst().getImageUrl();

            return new CartItemAdvancedResponse(
                    book.getId(),
                    bookImageUrl,
                    book.getTitle(),
                    book.getPrice(),
                    book.getSalesPrice(),
                    book.getStock(),
                    quantity
            );
        }).toList();

        return new CartItemListAdvancedResponse(Long.parseLong(cartId), items);
    }

    /**
     * 장바구니 내 특정 아이템의 수량을 업데이트함 (Write-Through)
     * <ul>
     *     <li>회원: 1. Redis 업데이트 → 2. DB 업데이트</li>
     *     <li>비회원: Redis만 업데이트</li>
     * </ul>
     */
    @Transactional
    public CartItemUpdateResponse updateCartItem(CartItemUpdateDto dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            throw new CartNotFoundException();
        }

        // 도서 재고 검증
        var book = this.bookRepository.findById(dto.bookId())
                .orElseThrow(BookNotFoundException::new);

        int stock = book.getStock();
        if (dto.quantity() > stock) {
            throw new BookInsufficientStockException();
        }

        // 1. Redis 업데이트 (회원/비회원, 우선 업데이트)
        this.cartRedisRepository.putCartItem(cartId, dto.bookId(), dto.quantity());

        // 2. DB 업데이트 (회원, Write-Through)
        if (Objects.nonNull(dto.memberId())) {
            this.cartSyncService.saveOrUpdateCartItem(dto.memberId(), dto.bookId(), dto.quantity());
        }

        return new CartItemUpdateResponse(dto.bookId(), dto.quantity());
    }

    /**
     * 장바구니에서 특정 도서 아이템을 삭제함 (Write-Through)
     * <ul>
     *     <li>회원: 1. Redis 삭제 → 2. DB 삭제</li>
     *     <li>비회원: Redis만 삭제</li>
     * </ul>
     */
    @Transactional
    public void deleteCartItem(CartItemDeleteDto dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            throw new CartNotFoundException();
        }

        // 해당 아이템이 장바구니에 존재하지 않는 경우
        Integer quantity = this.cartRedisRepository.getCartItem(cartId, dto.bookId());
        if (Objects.isNull(quantity)) {
            throw new CartItemNotFoundException();
        }

        // 1. Redis 삭제 (회원/비회원, 우선 삭제)
        this.cartRedisRepository.deleteCartItem(cartId, dto.bookId());

        // 2. DB 삭제 (회원, Write-Through)
        if (Objects.nonNull(dto.memberId())) {
            this.cartSyncService.deleteCartItem(dto.memberId(), dto.bookId());
        }
    }

    /**
     * 장바구니 내 모든 아이템을 삭제함 (Write-Through)
     * <ul>
     *     <li>회원: 1. Redis 전체 삭제 → 2. DB 전체 삭제</li>
     *     <li>비회원: Redis만 삭제</li>
     * </ul>
     */
    @Transactional
    public void deleteAllCartItems(CartItemDeleteAllDto dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            throw new CartNotFoundException();
        }

        // 1. Redis 전체 삭제 (회원/비회원, 우선 전체 삭제)
        this.cartRedisRepository.deleteAllCartItems(cartId);

        // 2. DB 전체 삭제 (회원, Write-Through)
        if (Objects.nonNull(dto.memberId())) {
            this.cartSyncService.deleteAllCartItems(dto.memberId());
        }
    }

    /**
     * 장바구니 내 모든 아이템 개수를 반환함 (Redis 조회)
     */
    @Transactional(readOnly = true)
    public CartItemCountResponse getCartItemCount(CartOwner dto) {
        // 장바구니 조회
        String cartId = this.resolveCartId(dto);

        // 장바구니가 없는 경우
        if (Objects.isNull(cartId)) {
            return new CartItemCountResponse(0);
        }

        // Redis에서 아이템 개수 조회
        int count = this.cartRedisRepository.getCartItemSize(cartId);
        return new CartItemCountResponse(count);
    }

    /**
     * 로그인 시 DB → Redis 로딩 (세션 초기화)
     * <ul>
     *     <li>1. DB에서 회원 장바구니 조회</li>
     *     <li>2. 비회원 장바구니와 병합</li>
     *     <li>3. Redis에 저장</li>
     * </ul>
     *
     * @param memberId 회원 ID
     * @param guestId 비회원 ID
     * @return 로딩된 cartId
     */
    @Transactional
    public String loadCartOnLogin(Long memberId, String guestId) {
        String memberOwnerId = memberId.toString();

        // 1. 회원 장바구니 조회 또는 생성
        String cartId = this.cartRedisRepository.findCartIdByOwner(memberOwnerId);
        if (Objects.isNull(cartId)) {
            cartId = this.cartRedisRepository.createCart(memberOwnerId);
        }
        final String finalCartId = cartId;

        // 2. DB에서 회원 장바구니 조회
        Map<Long, Integer> dbItems = this.cartSyncService.loadCartItemsFromDb(memberId);

        // 3. 비회원 장바구니 병합 (존재하는 경우)
        Map<Long, Integer> guestItems = Map.of();
        if (Objects.nonNull(guestId)) {
            String guestCartId = this.cartRedisRepository.findCartIdByOwner(guestId);
            if (Objects.nonNull(guestCartId)) {
                guestItems = this.cartRedisRepository.getAllCartItems(guestCartId);
                // 비회원 Redis 장바구니 삭제
                this.cartRedisRepository.deleteAllCartItems(guestCartId);
            }
        }

        // 4. DB 아이템을 Redis에 로딩
        dbItems.forEach((bookId, quantity) ->
                this.cartRedisRepository.putCartItem(finalCartId, bookId, quantity)
        );

        // 5. 비회원 아이템 병합 (수량은 더하기)
        guestItems.forEach((bookId, guestQuantity) -> {
            Integer currentQuantity = this.cartRedisRepository.getCartItem(finalCartId, bookId);
            int newQty = Objects.nonNull(currentQuantity) ? currentQuantity + guestQuantity : guestQuantity;

            // Redis + DB 동시 저장
            this.cartRedisRepository.putCartItem(finalCartId, bookId, newQty);
            this.cartSyncService.saveOrUpdateCartItem(memberId, bookId, newQty);
        });

        return finalCartId;
    }

    /**
     * 로그아웃 시 Redis만 삭제 (DB는 유지)
     * <p>DB에는 이미 모든 변경사항이 반영되어 있으므로 추가 작업 불필요</p>
     *
     * @param memberId 회원 ID
     */
    @Transactional
    public void clearCartOnLogout(Long memberId) {
        String ownerId = memberId.toString();
        String cartId = this.cartRedisRepository.findCartIdByOwner(ownerId);

        if (Objects.nonNull(cartId)) {
            // Redis 장바구니만 삭제 (세션 종료)
            this.cartRedisRepository.deleteAllCartItems(cartId);
        }
    }

    /* =========================== 내부 유틸 메서드 =========================== */

    private String resolveOwnerId(CartOwner dto) {
        if (Objects.nonNull(dto.memberId())) {
            return dto.memberId().toString();
        } else if (Objects.nonNull(dto.guestId())) {
            return dto.guestId();
        } else {
            throw new IllegalArgumentException("회원 ID 또는 비회원 ID 둘 중 하나는 반드시 존재해야 합니다.");
        }
    }

    private String resolveCartId(CartOwner dto) {
        return this.cartRedisRepository.findCartIdByOwner(this.resolveOwnerId(dto));
    }

    private String resolveOrCreateCartId(CartOwner dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            String ownerId = this.resolveOwnerId(dto);
            cartId = this.cartRedisRepository.createCart(ownerId);
        }
        return cartId;
    }
}