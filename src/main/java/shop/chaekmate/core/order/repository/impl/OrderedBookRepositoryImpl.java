package shop.chaekmate.core.order.repository.impl;

import static shop.chaekmate.core.order.entity.QOrderedBook.orderedBook;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.OrderedBook;
import shop.chaekmate.core.order.entity.QOrderedBook;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.repository.OrderedBookRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class OrderedBookRepositoryImpl implements OrderedBookRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<OrderedBook> findAllByOrder(Order orderEntity) {
        return queryFactory
                .selectFrom(orderedBook)
                .join(orderedBook.book).fetchJoin()
                .leftJoin(orderedBook.wrapper).fetchJoin()
                .where(orderedBook.order.eq(orderEntity))
                .fetch();
    }

    @Override
    public List<OrderedBook> findShippingBooks() {
        QOrderedBook ob = QOrderedBook.orderedBook;

        return queryFactory
                .selectFrom(ob)
                .where(
                        ob.unitStatus.eq(OrderedBookStatusType.SHIPPING),
                        ob.shippedAt.isNotNull()
                )
                .fetch();
    }

    @Override
    public boolean isAllBooksDelivered(Long orderId) {
        QOrderedBook ob = QOrderedBook.orderedBook;

        Integer count = queryFactory
                .selectOne()
                .from(ob)
                .where(
                        ob.order.id.eq(orderId),
                        ob.unitStatus.ne(OrderedBookStatusType.DELIVERED)
                )
                .fetchFirst();

        return count == null;
    }
}
