package shop.chaekmate.core.payment.service;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentErrorServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentErrorService paymentErrorService;

    @Test
    void 승인_실패시_에러이벤트_발행_및_로그기록() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS, null, "test_order_number_nanoid", 29800L, null
        );

        paymentErrorService.saveAbortedPayment(request, "실패 에러");

        verify(paymentHistoryRepository).save(any(PaymentHistory.class));
    }

    @Test
    void 승인_에러처리시_예외가_발생해도_중단되지_않음() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.POINT, null, "orderNumber123", 0L, 29800
        );

        doThrow(new RuntimeException("저장 실패"))
                .when(paymentHistoryRepository).save(any(PaymentHistory.class));

        assertThatCode(() -> paymentErrorService.saveAbortedPayment(request, "저장 실패"))
                .doesNotThrowAnyException();
    }
}
