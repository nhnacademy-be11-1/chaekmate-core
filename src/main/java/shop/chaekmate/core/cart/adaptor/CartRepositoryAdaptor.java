package shop.chaekmate.core.cart.adaptor;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.exception.BookNotFoundException;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.exception.cart.CartNotFoundException;
import shop.chaekmate.core.cart.model.CartItemSortCriteria;
import shop.chaekmate.core.cart.repository.CartItemRepository;
import shop.chaekmate.core.cart.repository.CartRepository;

@Service
@Primary    // CartStore 기본 구현체 지정
@RequiredArgsConstructor
public class CartRepositoryAdaptor implements CartStore {

    private final BookRepository bookRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    // 장바구니 조회
    @Override
    public Cart findCartByMemberId(Long memberId) {
        return this.cartRepository.findByMemberId(memberId).orElse(null);   // 추후 NPE 방지 위해 Optional 반환 수정 고려
    }

    // 장바구니 생성(저장)
    @Override
    public Cart saveCart(Cart cart) {
        return this.cartRepository.save(cart);
    }

    // 장바구니 삭제
    @Override
    public void deleteCart(Long memberId) {
        this.cartRepository.deleteByMemberId(memberId);
    }

    @Override
    public CartItem findItemById(Long cartItemId) {
        return this.cartItemRepository.findById(cartItemId).orElse(null);   // 추후 NPE 방지 위해 Optional 반환 수정 고려
    }

    // 장바구니 아이템 조회 - 정렬 조건에 따라 조회 (기본값: 최근순(생성일 내림차순))
    @Override
    public List<CartItem> findItemList(Long cartId, CartItemSortCriteria criteria) {
        return switch (criteria) {
            case BOOK_TITLE_ASC-> this.cartItemRepository.findAllByCartIdOrderByBookTitleAsc(cartId);
            case BOOK_TITLE_DESC -> this.cartItemRepository.findAllByCartIdOrderByBookTitleDesc(cartId);
            case CREATED_ASC -> this.cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(cartId);
            case CREATED_DESC -> this.cartItemRepository.findAllByCartIdOrderByCreatedAtDesc(cartId);
        };
    }

    // 장바구니 아이템 추가
    // 추후 리팩토링 필요 - 비즈니스 로직 분리 --> 서비스 계층
    @Override
    public CartItem saveOrUpdateItem(Long cartId, Long bookId, int quantity) {
        Book book = this.bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(String.format("Book id %s not found", bookId)));

        // 1. 추가하려는 아이템이 이미 담긴 아이템인 경우
        Optional<CartItem> itemOptional = this.cartItemRepository.findByCartIdAndBookId(cartId, bookId);
        if (itemOptional.isPresent()) {
            CartItem item = itemOptional.get();
            item.updateQuantity(quantity);
            return this.cartItemRepository.save(item);
        }

        // 2. 추가하려는 아이템이 새로운 아이템인 경우
        Cart cart = this.cartRepository.findById(cartId).orElseThrow(CartNotFoundException::new);
        CartItem newItem = CartItem.create(cart, book);
        newItem.updateQuantity(quantity);

        return this.cartItemRepository.save(newItem);
    }

    // 장바구니 아이템 삭제
    @Override
    public void removeItem(Long cartItemId) {
        this.cartItemRepository.deleteById(cartItemId);
        this.cartItemRepository.flush();
    }

    @Override
    public void removeAllItem(Long cartId) {
        this.cartItemRepository.deleteAllByCartId(cartId);
    }
}
