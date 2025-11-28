package shop.chaekmate.core.payment.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByOrderNumber(String orderNumber);

    Optional<Payment> findByOrderNumberAndPaymentKey(String orderNumber, String paymentKey);
}
