package shop.chaekmate.core.order.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.chaekmate.core.order.dto.response.MemberPureAmountDto;
import shop.chaekmate.core.order.entity.Order;
import shop.chaekmate.core.order.entity.type.OrderStatusType;
import shop.chaekmate.core.order.entity.type.OrderedBookStatusType;
import shop.chaekmate.core.order.repository.OrderRepositoryCustom;

import java.time.LocalDateTime;
import java.util.List;

import static shop.chaekmate.core.member.entity.QMember.member;
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

    public Page<Order> findAllOrders(OrderStatusType orderStatus,
                                     OrderedBookStatusType unitStatus,
                                     Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (orderStatus != null) {
            builder.and(order.status.eq(orderStatus));
        }

        if (unitStatus != null) {
            builder.and(order.id.in(
                    JPAExpressions.select(orderedBook.order.id)
                            .from(orderedBook)
                            .where(orderedBook.unitStatus.eq(unitStatus))
            ));
        }

        builder.and(validOrderCondition());

        List<Order> content = queryFactory
                .selectFrom(order)
                .where(builder)
                .orderBy(order.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(order.id.count())
                .from(order)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
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

    @Override
    public List<MemberPureAmountDto> calculateMemberPureAmounts(LocalDateTime startDate) {
        // 순수금액 = 상품 실구매액 합계 - 포장비 합계 - 배송비 합계
        // CANCELED, RETURNED 상태 제외

        // 포장비 합계 (NULL인 경우 0으로 처리)
        NumberExpression<Integer> totalWrapperPrice =
                orderedBook.wrapperPrice.coalesce(0).sum();

        // 각 주문의 배송비를 한 번만 계산하기 위한 표현식
        // 같은 주문의 OrderedBook들 중 첫 번째 것만 배송비를 카운트
        NumberExpression<Integer> deliveryFeeExpression = new CaseBuilder()
                .when(orderedBook.id.eq(
                        queryFactory
                                .select(orderedBook.id.min())
                                .from(orderedBook)
                                .where(orderedBook.order.eq(order))
                ))
                .then(order.deliveryFee)
                .otherwise(0);

        return queryFactory
                .select(Projections.constructor(
                        MemberPureAmountDto.class,
                        order.member.id,
                        orderedBook.totalPrice.sum()
                                .subtract(totalWrapperPrice)
                                .subtract(deliveryFeeExpression.sum())
                ))
                .from(orderedBook)
                .innerJoin(orderedBook.order, order)
                .innerJoin(order.member, member)
                .where(
                        order.createdAt.goe(startDate),
                        orderedBook.unitStatus.notIn(
                                OrderedBookStatusType.CANCELED,
                                OrderedBookStatusType.RETURNED
                        ),
                        order.member.id.isNotNull(),
                        member.deletedAt.isNull() // 탈퇴 회원 제외
                )
                .groupBy(order.member.id)
                .fetch();
    }
}
