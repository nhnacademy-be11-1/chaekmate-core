package shop.chaekmate.core.cart.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import shop.chaekmate.core.cart.entity.CartItem;
import shop.chaekmate.core.cart.entity.QCartItem;

@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 특정 Cart의 모든 장바구니 아이템 조회
     *
     * @param cartId Cart ID
     * @return 해당 Cart의 모든 장바구니 아이템 리스트
     */
    @Override
    public List<CartItem> findAllByCartId(Long cartId) {

        QCartItem cartItem = QCartItem.cartItem;

        return this.queryFactory
                .selectFrom(cartItem)
                .where(cartItem.cart.id.eq(cartId))
                .where(cartItem.deletedAt.isNull())
                .fetch();
    }
}
