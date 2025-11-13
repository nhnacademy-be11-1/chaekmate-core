package shop.chaekmate.core.cart.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.adaptor.CartStore;
import shop.chaekmate.core.cart.dto.request.CartDto;
import shop.chaekmate.core.cart.dto.request.CartItemDto;
import shop.chaekmate.core.cart.dto.response.CartItemListResponse;
import shop.chaekmate.core.cart.dto.response.CartItemSingleResponse;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.exception.cart.CartNotFoundException;
import shop.chaekmate.core.cart.exception.cart.MemberNotFoundException;
import shop.chaekmate.core.cart.exception.cartitem.BookInsufficientStockException;
import shop.chaekmate.core.cart.exception.cartitem.BookNotFoundException;
import shop.chaekmate.core.cart.model.CartItemSortCriteria;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CartServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartStore cartStore;

    @InjectMocks
    private CartService cartService;

    private Member member;
    private Book bookA;
    private Book bookB;

    private Cart cart;

    @BeforeEach
    void setup() {
        this.member = new Member(
                "testId",
                "testPassword",
                "testName",
                "010-1234-5678",
                "test@examplet.com",
                LocalDate.of(1998, 1, 1),
                PlatformType.LOCAL);

        this.bookA = Book.builder()
                .title("A")
                .index("목차")
                .description("설명")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .isbn("9781234567890")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(100)
                .build();

        this.bookB = Book.builder()
                .title("B")
                .index("목차")
                .description("설명")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .isbn("9781234567890")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(0)
                .build();

        this.cart = Cart.create(this.member);
    }

    @Test
    void 장바구니_삭제_성공() {
        CartDto dto = new CartDto(this.member.getId());
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));

        this.cartService.deleteCart(dto);

        verify(this.cartStore, times(1)).deleteCart(dto.memberId());
    }

    @Test
    void 장바구니_삭제_실패_존재하지않음() {
        CartDto dto = new CartDto(this.member.getId());
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

        this.cartService.deleteCart(dto);

        verify(this.cartStore, never()).deleteCart(dto.memberId());
    }

    @Test
    void 장바구니_아이템_추가_성공_장바구니_생성O() {
        // 명확한 상황 표현을 위해 DTO cartId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(this.member.getId(), null, this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(null);

        Cart newCart = Cart.create(this.member);
        when(this.cartStore.saveCart(any(Cart.class))).thenReturn(newCart);

        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.of(this.bookA));

        CartItem newItem = CartItem.create(newCart, this.bookA);
        newItem.updateQuantity(dto.quantity());
        when(this.cartStore.addItem(newCart.getId(), dto.bookId(), dto.quantity())).thenReturn(newItem);

        CartItemSingleResponse response = this.cartService.addCartItem(dto);

        // 응답 생성 여부 검증
        assertNotNull(response);

        // 핵심 호출 검증
        verify(this.cartStore, times(1)).saveCart(any(Cart.class));
        verify(this.cartStore, times(1)).addItem(newCart.getId(), dto.bookId(), dto.quantity());
    }

    @Test
    void 장바구니_아이템_추가_성공_장바구니_생성X() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.of(this.bookA));

        CartItem newItem = CartItem.create(this.cart, this.bookA);
        newItem.updateQuantity(dto.quantity());
        when(this.cartStore.addItem(this.cart.getId(), dto.bookId(), dto.quantity())).thenReturn(newItem);

        CartItemSingleResponse response = this.cartService.addCartItem(dto);

        // 응답 생성 여부 검증
        assertNotNull(response);

        // 핵심 호출 검증
        verify(this.cartStore, never()).saveCart(any(Cart.class));
        verify(this.cartStore, times(1)).addItem(this.cart.getId(), dto.bookId(), dto.quantity());
    }

    @Test
    void 장바구니_아이템_추가_실패_회원_존재X() {
        // 명확한 상황 표현을 위해 DTO memberId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(null, this.cart.getId(), this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

        // 예외 처리 검증
        assertThrows(MemberNotFoundException.class, () -> {
           this.cartService.addCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).findCartByMemberId(anyLong());
        verify(this.cartStore, never()).saveCart(any(Cart.class));
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 장바구니_아이템_추가_실패_도서_존재X() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookB.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.empty());

        // 예외 처리 검증
        assertThrows(BookNotFoundException.class, () -> {
            this.cartService.addCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 장바구니_아이템_추가_실패_도서_재고_부족() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookB.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.of(this.bookB));

        // 예외 처리 검증
        assertThrows(BookInsufficientStockException.class, () -> {
            this.cartService.addCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    // 장바구니 아이템 수량 업데이트
    @Test
    void 장바구니_아이템_수량_업데이트_성공() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookA.getId(), 3);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.of(this.bookA));

        CartItem newItem = CartItem.create(this.cart, this.bookA);
        newItem.updateQuantity(dto.quantity());
        when(this.cartStore.addItem(this.cart.getId(), dto.bookId(), dto.quantity())).thenReturn(newItem);

        CartItemSingleResponse response = this.cartService.updateCartItem(dto);

        // 응답 생성 여부 검증
        assertNotNull(response);

        // 핵심 호출 검증
        verify(this.cartStore, times(1)).addItem(this.cart.getId(), dto.bookId(), dto.quantity());
    }

    @Test
    void 장바구니_아이템_수량_업데이트_실패_회원_존재X() {
        // 명확한 상황 표현을 위해 DTO memberId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(null, this.cart.getId(), this.bookA.getId(), 3);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

        // 예외 처리 검증
        assertThrows(MemberNotFoundException.class, () -> {
            this.cartService.updateCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).findCartByMemberId(anyLong());
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 장바구니_아이템_수량_업데이트_실패_장바구니_존재X() {
        // 명확한 상황 표현을 위해 DTO cartId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(this.member.getId(), null, this.bookA.getId(), 3);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(this.member.getId())).thenReturn(null);

        // 예외 처리 검증
        assertThrows(CartNotFoundException.class, () -> {
            this.cartService.updateCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 장바구니_아이템_수량_업데이트_실패_도서_존재X() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookB.getId(), 3);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(this.member.getId())).thenReturn(this.cart);

        // 예외 처리 검증
        assertThrows(BookNotFoundException.class, () -> {
            this.cartService.updateCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    @Test
    void 장바구니_아이템_수량_업데이트_실패_도서_재고_부족() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookB.getId(), 3);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(this.member.getId())).thenReturn(this.cart);
        when(this.bookRepository.findById(dto.bookId())).thenReturn(Optional.of(this.bookB));

        // 예외 처리 검증
        assertThrows(BookInsufficientStockException.class, () -> {
            this.cartService.updateCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).addItem(anyLong(), anyLong(), anyInt());
    }

    // 장바구니 아이템 삭제 - 단일 아이템 삭제
    @Test
    void 단일_장바구니_아이템_삭제_성공() {
        CartItemDto dto = new CartItemDto(this.member.getId(), this.cart.getId(), this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        this.cartService.deleteCartItem(dto);

        // 핵심 호출 검증
        verify(this.cartStore, times(1)).removeItem(this.cart.getId(), dto.bookId());
    }

    @Test
    void 단일_장바구니_아이템_삭제_실패_회원_존재X() {
        // 명확한 상황 표현을 위해 DTO memberId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(null, this.cart.getId(), this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

        // 예외 처리 검증
        assertThrows(MemberNotFoundException.class, () -> {
            this.cartService.deleteCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).findCartByMemberId(anyLong());
        verify(this.cartStore, never()).removeItem(anyLong(), anyLong());
    }

    @Test
    void 단일_장바구니_아이템_삭제_실패_장바구니_존재X() {
        // 명확한 상황 표현을 위해 DTO cartId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartItemDto dto = new CartItemDto(this.member.getId(), null, this.bookA.getId(), 1);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(this.member.getId())).thenReturn(null);

        // 예외 처리 검증
        assertThrows(CartNotFoundException.class, () -> {
            this.cartService.deleteCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).removeItem(anyLong(), anyLong());
    }

    // 장바구니 아이템 삭제 - 전체 아이템 삭제(장바구니 비우기)
    @Test
    void 전체_장바구니_아이템_삭제_성공() {
        CartDto dto = new CartDto(this.member.getId());
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        this.cartService.deleteAllCartItem(dto);

        // 핵심 호출 검증
        verify(this.cartStore, times(1)).removeAllItem(this.cart.getId());
    }

    @Test
    void 전체_장바구니_아이템_삭제_실패_회원_존재X() {
        // 명확한 상황 표현을 위해 DTO memberId 값을 null 로 설정 (실제 DTO 는 NotNull)
        CartDto dto = new CartDto(null);
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.empty());

        // 예외 처리 검증
        assertThrows(MemberNotFoundException.class, () -> {
            this.cartService.deleteAllCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).findCartByMemberId(anyLong());
        verify(this.cartStore, never()).removeAllItem(anyLong());
    }

    @Test
    void 전체_장바구니_아이템_삭제_실패_장바구니_존재X() {
        CartDto dto = new CartDto(this.member.getId());
        when(this.memberRepository.findById(dto.memberId())).thenReturn(Optional.of(this.member));
        when(this.cartStore.findCartByMemberId(this.member.getId())).thenReturn(null);

        // 예외 처리 검증
        assertThrows(CartNotFoundException.class, () -> {
            this.cartService.deleteAllCartItem(dto);
        });

        // 핵심 호출 검증
        verify(this.cartStore, never()).removeAllItem(anyLong());
    }

    // 장바구니 아이템 조회 - 정렬 기준: 기본(최근순)
    @Test
    void 장바구니_아이템_목록_조회_성공() {
        CartDto dto = new CartDto(this.member.getId());
        when(this.cartStore.findCartByMemberId(dto.memberId())).thenReturn(this.cart);

        CartItemListResponse response = this.cartService.getCartItemList(dto);

        assertNotNull(response);

        verify(this.cartStore, times(1)).findItemList(this.cart.getId(), CartItemSortCriteria.CREATED_DESC);
    }
}
