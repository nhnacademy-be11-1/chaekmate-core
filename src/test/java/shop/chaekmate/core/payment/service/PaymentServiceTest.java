package shop.chaekmate.core.payment.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentType;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;

@Profile("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentServiceTest {

    @Mock
    private PaymentProviderFactory providerFactory;

    @Mock
    private PaymentProvider provider;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void 결제_승인_성공_결제내역_저장_승인_이벤트_발행() {
        // given
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentType.TOSS,
                "test_payment_key_123",
                "ORDER-20251104-001",
                29800
        );

        PaymentApproveResponse response = new PaymentApproveResponse(
                request.orderNumber(),
                request.paymentType().name(),
                request.amount(),
                "DONE",
                LocalDateTime.now()
        );

        when(providerFactory.getProvider(PaymentType.TOSS)).thenReturn(provider);
        when(provider.getType()).thenReturn(PaymentType.TOSS);
        when(provider.approve(any(PaymentApproveRequest.class))).thenReturn(response);

        // when
        PaymentApproveResponse result = paymentService.approve(request);
        System.out.println(result);
        // then
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class)); // ✅ 통과
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
        verify(eventPublisher, never()).publishPaymentFailed(any());
    }


    @Test
    @DisplayName("결제 승인 실패 시 실패내역이 저장되고 실패 이벤트가 발행된다")
    void approve_fail() {
        // given
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentType.TOSS,
                "test_payment_key_fail",
                "ORDER-20251104-999",
                29800
        );

        when(providerFactory.getProvider(PaymentType.TOSS)).thenReturn(provider);
        when(provider.approve(any())).thenThrow(new RuntimeException("결제 승인 실패 (테스트)"));

        // when
        try {
            paymentService.approve(request);
        } catch (RuntimeException e) {
            // expected
        }

        // then
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentFailed(any());
        verify(eventPublisher, never()).publishPaymentApproved(any());
    }
}

