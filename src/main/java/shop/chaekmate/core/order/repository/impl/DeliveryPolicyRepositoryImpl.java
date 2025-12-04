package shop.chaekmate.core.order.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.entity.QDeliveryPolicy;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class DeliveryPolicyRepositoryImpl implements DeliveryPolicyRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<DeliveryPolicy> findPolicyAt(LocalDateTime payTime) {
        QDeliveryPolicy p = QDeliveryPolicy.deliveryPolicy;

        DeliveryPolicy result = queryFactory
                .selectFrom(p)
                .where(
                        p.createdAt.loe(payTime),
                        p.deletedAt.isNull()
                                .or(p.deletedAt.gt(payTime))
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
