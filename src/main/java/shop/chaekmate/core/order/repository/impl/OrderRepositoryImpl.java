package shop.chaekmate.core.order.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.repository.OrderRepositoryCustom;

import static shop.chaekmate.core.order.entity.QOrder.order;
import static shop.chaekmate.core.order.entity.QOrderedBook.orderedBook;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchNonMemberOrder(String orderNumber, String ordererName, String ordererPhone, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(orderNumber)) {
            builder.and(order.orderNumber.eq(orderNumber));
        }
        if (StringUtils.hasText(ordererName)) {
            builder.and(order.ordererName.eq(ordererName));
        }
        if (StringUtils.hasText(ordererPhone)) {
            builder.and(order.ordererPhone.eq(ordererPhone));
        }

        if (!builder.hasValue()) {
            return Page.empty(pageable);
        }

        builder.and(validOrderCondition());

        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .where(builder)
                .orderBy(order.createdAt.desc());

        List<Order> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(builder);

        return new PageImpl<>(content, pageable, countQuery.fetchOne());
    }

    @Override
    public Page<Order> findMemberOrders(Long memberId, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(order.member.id.eq(memberId));
        builder.and(validOrderCondition());

        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .where(builder)
                .orderBy(order.createdAt.desc());

        List<Order> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .where(builder);

        return new PageImpl<>(content, pageable, countQuery.fetchOne());
    }

    private BooleanBuilder validOrderCondition() {
        return new BooleanBuilder().and(order.id.in(
                JPAExpressions.select(orderedBook.order.id)
                        .from(orderedBook)
                        .where(orderedBook.unitStatus.notIn(
                                OrderedBookStatusType.PAYMENT_READY,
                                OrderedBookStatusType.PAYMENT_FAILED
                        ))
        ));
    }
}
