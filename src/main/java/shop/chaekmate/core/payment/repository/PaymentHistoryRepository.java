package shop.chaekmate.core.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.payment.entity.PaymentHistory;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long>, PaymentHistoryRepositoryCustom {
}
