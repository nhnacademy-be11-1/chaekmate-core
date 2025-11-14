package shop.chaekmate.core.payment.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;

public interface PaymentHistoryRepositoryCustom {

    Page<PaymentHistoryDto> findHistoriesByFilter(
            PaymentMethodType paymentType,
            PaymentStatusType paymentStatus,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
