package shop.chaekmate.core.payment.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.event.PaymentEventPublisher;
import shop.chaekmate.core.payment.exception.NotFoundOrderNumberException;
import shop.chaekmate.core.payment.provider.PaymentProvider;
import shop.chaekmate.core.payment.provider.PaymentProviderFactory;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;
import shop.chaekmate.core.payment.repository.PaymentRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentServiceTest {

    @Mock
    private PaymentProviderFactory providerFactory;

    @Mock
    private PaymentProvider provider;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentErrorService paymentErrorService;

    @Test
    void 외부API_결제_승인_성공_저장_및_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS, "test_payment_key_random", "test_order_number_nanoid", 29800, null);

        PaymentApproveResponse response = new PaymentApproveResponse(
                "test_order_number_nanoid", "test_payment_key_random", 29800, "APPROVED", OffsetDateTime.now());

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);
        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentApproveResponse result = paymentService.approve(request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("APPROVED", result.status()),
                () -> assertEquals("test_order_number_nanoid", result.orderNumber()),
                () -> assertEquals(29800, result.totalAmount())
        );

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }

    @Test
    void 포인트_결제_승인_성공_저장_및_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.POINT,
                null,
                "test_order_number_nanoid",
                0L,
                29800
        );

        when(paymentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(paymentHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentApproveResponse result = paymentService.approve(request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("APPROVED", result.status()),
                () -> assertEquals("test_order_number_nanoid", result.orderNumber()),
                () -> assertEquals(29800, result.totalAmount())
        );

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentApproved(any(PaymentApproveResponse.class));
    }

    @Test
    void 결제_승인_실패() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS, "test_payment_key_random", "test_order_number_nanoid", 29800, null);

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenThrow(new RuntimeException("결제 실패"));

        PaymentApproveResponse response = paymentService.approve(request);

        assertNotNull(response);
        assertAll(
                () -> assertEquals("ABORTED", response.status()),
                () -> assertEquals("결제 실패", response.paymentKey())
        );

        verify(eventPublisher, never()).publishPaymentApproved(any());
    }

    @Test
    void 포인트_결제_취소_성공_상태_변경_및_저장_이벤트() {
        Payment payment = Payment.createApproved(
                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.POINT, 0L, 29800);

        when(paymentRepository.findByOrderNumber("test_order_number_nanoid")).thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                "test_payment_key_random", "test_order_number_nanoid", "환불", 29800L);

        PaymentCancelResponse response = paymentService.cancel(request);

        assertNotNull(response);
        assertAll(
                () -> assertEquals("CANCELED", response.status()),
                () -> assertEquals("환불", response.cancelReason())
        );

        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(any());
    }

    @Test
    void 외부API_결제_취소_상태_변경_및_저장_이벤트() {
        when(providerFactory.getProvider(any())).thenReturn(provider);

        Payment payment = Payment.createApproved(
                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.TOSS, 29800L, 0);

        when(paymentRepository.findByOrderNumber("test_order_number_nanoid"))
                .thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                "test_payment_key_random", "test_order_number_nanoid", "고객 변심", 29800L);

        PaymentCancelResponse response = new PaymentCancelResponse(
                "test_order_number_nanoid", "고객 변심", 29800L, "CANCELED", OffsetDateTime.now());

        when(provider.cancel(any())).thenReturn(response);

        PaymentCancelResponse result = paymentService.cancel(request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("CANCELED", result.status()),
                () -> assertEquals("고객 변심", result.cancelReason())
        );

        verify(provider, times(1)).cancel(any());
        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(response);
    }

    @Test
    void 결제_취소_실패() {
        when(providerFactory.getProvider(any())).thenReturn(provider);

        Payment payment = Payment.createApproved(
                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.TOSS, 29800L, null);

        when(paymentRepository.findByOrderNumber("test_order_number_nanoid"))
                .thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                "test_payment_key_random", "test_order_number_nanoid", "서버 오류", 29800L);

        when(provider.cancel(any())).thenThrow(new RuntimeException("서버 오류"));

        assertThatThrownBy(() -> paymentService.cancel(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("결제 취소 실패");

        verify(paymentHistoryRepository, never()).save(any());
    }

    @Test
    void 존재하지_않는_주문번호로_취소시_예외_발생() {
        when(paymentRepository.findByOrderNumber("unknown")).thenReturn(Optional.empty());
        PaymentCancelRequest request = new PaymentCancelRequest(
                "test_payment_key_random", "unknown", "테스트", 29800L
        );

        assertThatThrownBy(() -> paymentService.cancel(request))
                .isInstanceOf(NotFoundOrderNumberException.class)
                .hasMessageContaining("해당 주문번호가 존재하지 않습니다.");
    }
}
