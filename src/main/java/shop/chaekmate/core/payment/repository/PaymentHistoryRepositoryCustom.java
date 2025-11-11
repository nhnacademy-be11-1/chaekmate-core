package shop.chaekmate.core.payment.repository;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;

public interface PaymentHistoryRepositoryCustom {

    Page<PaymentHistoryDto> findHistoriesByFilter(
            PaymentMethodType paymentType,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}
