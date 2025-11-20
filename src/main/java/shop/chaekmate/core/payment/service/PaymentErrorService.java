package shop.chaekmate.core.payment.service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentErrorService {

    private final PaymentRepository paymentRepository;

    private final PaymentHistoryRepository paymentHistoryRepository;

    // 트랜 잭션 분리(rollback x)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAbortedPayment(PaymentApproveRequest request, String reason) {
        try{
            Payment payment = Payment.createAborted(
                    request.orderNumber(),
                    null,    // 결제 실패 시 결제키 없음
                    request.paymentType(),
                    request.amount()
            );
            paymentRepository.save(payment);

            PaymentHistory history = PaymentHistory.aborted(payment, request.amount(), reason, LocalDateTime.now());
            paymentHistoryRepository.save(history);

            log.warn("[결제 실패 로그 저장 완료] 주문번호={}, 금액={}, 이유={}",
                    payment.getOrderNumber(), payment.getTotalAmount(), reason);
        }
        catch (Exception e){
            log.error("[결제 실패 로그 저장 중 오류 발생] orderNumber={}, cause={}", request.orderNumber(), e.getMessage());

        }
    }
}
