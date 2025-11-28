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
 * 회원/비회원 공통으로 CartOwner DTO 를 기반으로 장바구니를 조회하며,
 * Redis 기반 CartRedisRepository 를 활용하여 장바구니 아이템 CRUD 작업을 수행함
 */

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final BookRepository bookRepository;
    private final BookImageRepository bookImageRepository;

    /**
     * 장바구니 아이템을 추가함 (수량은 항상 1로 고정)
     * <ul>
     *     <li>장바구니가 존재하지 않으면 자동 생성</li>
     *     <li>이미 존재하는 도서인 경우 Redis HashOperations.put 으로 수량 overwrite</li>
     *     <li>최종적으로 장바구니 전체 목록을 반환</li>
     * </ul>
     *
     * @param dto 생성 요청 DTO (owner + bookId 포함)
     * @return 장바구니 전체 아이템 목록 응답 DTO
     */
    @Transactional
    public CartItemListResponse addCartItem(CartItemCreateDto dto) {

        // 장바구니 조회
        // 존재하지 않는 경우, 장바구니 생성
        String cartId = this.resolveOrCreateCartId(dto);

        // 도서 재고 검증
        var book = this.bookRepository.findById(dto.bookId())
                .orElseThrow(BookNotFoundException::new);

        int stock = book.getStock();
        if (dto.quantity() > stock) {
            throw new BookInsufficientStockException();
        }

        // 장바구니 아이템 생성 및 추가
        this.cartRedisRepository.putCartItem(cartId, dto.bookId(), dto.quantity());

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
     *     <li>장바구니가 존재하지 않으면 자동 생성</li>
     *     <li>도서 존재 여부 검증: bookRepository.findById → BookNotFoundException</li>
     *     <li>대표 이미지: createdAt 기준 오름차순 1번째 이미지</li>
     * </ul>
     *
     * @param dto 장바구니 소유자 정보 DTO
     * @return 도서 상세 정보가 포함된 장바구니 아이템 목록 응답 DTO
     * @throws BookNotFoundException 도서가 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public CartItemListAdvancedResponse getCartItemsWithBookInfo(CartOwner dto) {
        // 장바구니 조회
        // 존재하지 않는 경우, 장바구니 생성
        String cartId = this.resolveOrCreateCartId(dto);

        // 장바구니 아이템 전체 조회
        // - bookId --> quantity
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
            String bookImageUrl = images.isEmpty() ? null : images.get(0).getImageUrl();

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
     * 장바구니 내 특정 아이템의 수량을 업데이트함
     * <ul>
     *     <li>Redis의 putCartItem을 사용하여 해당 key의 수량을 그대로 저장</li>
     * </ul>
     *
     * @param dto bookId 및 새로운 quantity를 포함한 DTO
     * @return 업데이트 결과 응답 DTO
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

        this.cartRedisRepository.putCartItem(cartId, dto.bookId(), dto.quantity());

        return new CartItemUpdateResponse(dto.bookId(), dto.quantity());
    }

    /**
     * 장바구니에서 특정 도서 아이템을 삭제함
     * @param dto 삭제 요청 DTO (owner + bookId)
     * @throws CartItemNotFoundException 장바구니에 해당 아이템이 존재하지 않는 경우
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

        this.cartRedisRepository.deleteCartItem(cartId, dto.bookId());
    }

    /**
     * 장바구니 내 모든 아이템을 삭제함
     * <p>
     * 장바구니는 유지되며 아이템 Hash만 비워짐
     *
     * @param dto owner 정보 DTO
     */
    @Transactional
    public void deleteAllCartItems(CartItemDeleteAllDto dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            throw new CartNotFoundException();
        }

        this.cartRedisRepository.deleteAllCartItems(cartId);
    }

    /**
     * 장바구니 내 모든 아이템 개수를 반환함
     *
     * @param dto bookId 및 새로운 quantity를 포함한 DTO
     * @return 장바구니 아이템 (종류) 개수 결과 응답 DTO
     */
    @Transactional(readOnly = true)
    public CartItemCountResponse getCartItemCount(CartOwner dto) {
        // 장바구니 조회
        String cartId = this.resolveCartId(dto);

        // 장바구니가 없는 경우
        if (Objects.isNull(cartId)) {
            // 예외 발생 대신 0개 응답이 더 자연스러움
            return new CartItemCountResponse(0);
        }

        // 장바구니 내 모든 아이템 개수 계산
        // - 도서 종류 개수 반환
        // -- 예시: 책A 2권, 책B 3권 --> 2 종류 --> count = 2
        int count =  this.cartRedisRepository.getCartItemSize(cartId);
        return new CartItemCountResponse(count);
    }

    /**
     * CartOwner DTO 로부터 ownerId (memberId 또는 guestId)를 추출함
     * <ul>
     *     <li>회원: memberId 사용</li>
     *     <li>비회원: guestId 사용</li>
     *     <li>둘 다 null 인 경우 예외 발생</li>
     * </ul>
     *
     * @param dto CartOwner DTO
     * @return ownerId 문자열
     * @throws IllegalArgumentException ID 정보가 없는 경우
     */
    private String resolveOwnerId(CartOwner dto) {
        // 회원ID 존재하는 경우 (로그인O), 우선 적용 --> 즉, Guest ID 무시
        if (Objects.nonNull(dto.memberId())) {
            return dto.memberId().toString();
        } else if (Objects.nonNull(dto.guestId())) {
            return dto.guestId();
        } else {
            throw new IllegalArgumentException("회원 ID 또는 비회원 ID 둘 중 하나는 반드시 존재해야 합니다.");
        }
    }

    /**
     * ownerId 기반으로 Redis 에 저장된 장바구니 ID(cartId)를 조회함
     * <ul>
     *     <li>장바구니가 존재하지 않으면 null 반환</li>
     * </ul>
     *
     * @param dto CartOwner DTO
     * @return cartId (문자열) 또는 null
     */
    private String resolveCartId(CartOwner dto) {
        return this.cartRedisRepository.findCartIdByOwner(this.resolveOwnerId(dto));
    }

    /**
     * 장바구니 ID(cartId)를 조회하며, 존재하지 않을 경우 자동으로 생성함
     * <p>쓰기 작업이 필요한 메서드에서만 사용</p>
     *
     * @param dto CartOwner DTO
     * @return 생성되었거나 기존에 존재하던 cartId 문자열
     */
    private String resolveOrCreateCartId(CartOwner dto) {
        String cartId = this.resolveCartId(dto);
        if (Objects.isNull(cartId)) {
            String ownerId = this.resolveOwnerId(dto);
            cartId = this.cartRedisRepository.createCart(ownerId);
        }
        return cartId;
    }

}
