package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {

    private final PaymentHistoryRepository paymentHistoryRepository;

    // 전체 결제 내역 조회
    // 필터 1.결제수단(toss, payco등)
    // 필터 2.시작~종료 사이
    @Transactional(readOnly = true)
    public Page<PaymentHistoryDto> findHistoriesByFilter(
            PaymentMethodType paymentType,
            PaymentStatusType paymentStatus,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    ) {
        return paymentHistoryRepository.findHistoriesByFilter(paymentType, paymentStatus, start, end, pageable);
    }
}
