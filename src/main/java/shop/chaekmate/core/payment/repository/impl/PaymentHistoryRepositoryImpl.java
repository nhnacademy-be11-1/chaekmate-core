package shop.chaekmate.core.payment.repository.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.QPayment;
import shop.chaekmate.core.payment.entity.QPaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryRepositoryImpl implements PaymentHistoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PaymentHistoryDto> findHistoriesByFilter(
            PaymentMethodType paymentType,
            PaymentStatusType paymentStatus,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        QPaymentHistory ph = QPaymentHistory.paymentHistory;
        QPayment p = QPayment.payment;

        BooleanBuilder condition = new BooleanBuilder();

        if (paymentType != null) {
            condition.and(p.paymentType.eq(paymentType));
        }

        if (paymentStatus != null) {
            condition.and(ph.paymentStatus.eq(paymentStatus));
        }

        if (start != null && end != null) {
            condition.and(ph.occurredAt.between(start, end));
        } else if (start != null) {
            condition.and(ph.occurredAt.goe(start));
        } else if (end != null) {
            condition.and(ph.occurredAt.loe(end));
        }

        List<PaymentHistoryDto> results = queryFactory
                .select(Projections.constructor(
                        PaymentHistoryDto.class,
                        p.orderNumber,
                        p.paymentType,
                        ph.paymentStatus,
                        ph.totalAmount,
                        ph.reason,
                        ph.occurredAt
                ))
                .from(ph)
                .join(ph.payment, p)
                .where(condition)
                .orderBy(ph.occurredAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = Optional.ofNullable(
                queryFactory
                        .select(ph.count())
                        .from(ph)
                        .join(ph.payment, p)
                        .where(condition)
                        .fetchOne()
        ).orElse(0L);

        return new PageImpl<>(results, pageable, total);
    }
}
