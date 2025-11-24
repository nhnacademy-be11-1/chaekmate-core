package shop.chaekmate.core.order.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import shop.chaekmate.core.order.dto.request.NonMemberOrderHistoryRequest;
import shop.chaekmate.core.order.entity.Order;

import static shop.chaekmate.core.order.entity.QOrder.order;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> searchNonMemberOrder(NonMemberOrderHistoryRequest request, Pageable pageable) {
        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(request.orderNumber())) {
            builder.and(order.orderNumber.eq(request.orderNumber()));
        }
        if (StringUtils.hasText(request.ordererName())) {
            builder.and(order.ordererName.eq(request.ordererName()));
        }
        if (StringUtils.hasText(request.ordererPhone())) {
            builder.and(order.ordererPhone.eq(request.ordererPhone()));
        }

        // At least one condition must be present
        if (!builder.hasValue()) {
            return Page.empty(pageable);
        }

        JPAQuery<Order> query = queryFactory
                .selectFrom(order)
                .where(builder);

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
}
