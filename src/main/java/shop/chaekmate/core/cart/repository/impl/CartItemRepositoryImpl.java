package shop.chaekmate.core.cart.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.book.entity.QBook;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.entity.QCartItem;
import shop.chaekmate.core.cart.repository.CartItemRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<CartItem> findAllByCartIdOrderByBookTitleAsc(Long cartId) {
        QCartItem cartItem = QCartItem.cartItem;
        QBook book = QBook.book;

        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.book, book).fetchJoin()
                .where(cartItem.cart.id.eq(cartId))
                .orderBy(book.title.asc())
                .fetch();
    }

    @Override
    public List<CartItem> findAllByCartIdOrderByBookTitleDesc(Long cartId) {
        QCartItem cartItem = QCartItem.cartItem;
        QBook book = QBook.book;

        return queryFactory
                .selectFrom(cartItem)
                .join(cartItem.book, book).fetchJoin()
                .where(cartItem.cart.id.eq(cartId))
                .orderBy(book.title.desc())
                .fetch();
    }
}
