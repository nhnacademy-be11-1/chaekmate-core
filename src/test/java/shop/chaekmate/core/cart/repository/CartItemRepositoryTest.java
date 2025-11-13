package shop.chaekmate.core.cart.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.entity.Book;
import shop.chaekmate.core.book.repository.BookRepository;
import shop.chaekmate.core.cart.entity.Cart;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.common.config.JpaAuditingConfig;
import shop.chaekmate.core.common.config.QueryDslConfig;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.entity.type.PlatformType;
import shop.chaekmate.core.member.repository.MemberRepository;

@DataJpaTest
@ActiveProfiles("test")
@Import({QueryDslConfig.class, JpaAuditingConfig.class})
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BookRepository bookRepository;

    private Member member;
    private Cart cart;
    private Book bookA;
    private Book bookB;
    private Book bookC;

    @BeforeEach
    void setup() {
        this.member = new Member("testId", "testPassword", "testName", "010-1234-5678", "test@examplet.com", LocalDate.of(1998, 1, 1), PlatformType.LOCAL);
        this.memberRepository.save(member);

        this.cart = Cart.create(member);
        this.cartRepository.save(cart);

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
                .isbn("9781234567891")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(100)
                .build();

        this.bookC = Book.builder()
                .title("C")
                .index("목차")
                .description("설명")
                .author("테스트 저자")
                .publisher("테스트 출판사")
                .publishedAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .isbn("9781234567892")
                .price(10000)
                .salesPrice(9000)
                .isWrappable(true)
                .views(0)
                .isSaleEnd(false)
                .stock(100)
                .build();

        this.bookRepository.save(this.bookA);
        this.bookRepository.save(this.bookB);
        this.bookRepository.save(this.bookC);

        CartItem item1 = CartItem.create(this.cart, this.bookA);
        CartItem item2 = CartItem.create(this.cart, this.bookB);
        CartItem item3 = CartItem.create(this.cart, this.bookC);

        this.cartItemRepository.save(item1);
        this.cartItemRepository.save(item2);
        this.cartItemRepository.save(item3);
    }

    @Test
    void 도서명_기준_장바구니_아이템_오름차순_조회() {
        List<CartItem> items = this.cartItemRepository.findAllByCartIdOrderByCreatedAtAsc(this.cart.getId());

        assertThat(items).hasSize(3);
        assertThat(items.get(0).getBook().getTitle()).isEqualTo("A");
        assertThat(items.get(1).getBook().getTitle()).isEqualTo("B");
        assertThat(items.get(2).getBook().getTitle()).isEqualTo("C");
    }

    @Test
    void 도서명_기준_장바구니_아이템_내림차순_조회() {
        List<CartItem> items = this.cartItemRepository.findAllByCartIdOrderByCreatedAtDesc(this.cart.getId());

        assertThat(items).hasSize(3);
        assertThat(items.get(0).getBook().getTitle()).isEqualTo("C");
        assertThat(items.get(1).getBook().getTitle()).isEqualTo("B");
        assertThat(items.get(2).getBook().getTitle()).isEqualTo("A");
    }
}
