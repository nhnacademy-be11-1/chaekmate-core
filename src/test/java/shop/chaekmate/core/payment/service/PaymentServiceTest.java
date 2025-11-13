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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.order.dto.request.CanceledBooksRequest;
import shop.chaekmate.core.payment.dto.request.PaymentApproveRequest;
import shop.chaekmate.core.payment.dto.request.PaymentCancelRequest;
import shop.chaekmate.core.payment.dto.response.base.PaymentResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentAbortedResponse;
import shop.chaekmate.core.payment.dto.response.impl.PaymentApproveResponse;
import shop.chaekmate.core.payment.dto.response.PaymentCancelResponse;
import shop.chaekmate.core.payment.entity.Payment;
import shop.chaekmate.core.payment.entity.PaymentHistory;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
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

    List<CanceledBooksRequest> canceledBooks;

    @BeforeEach
    void setUp() {
        canceledBooks = List.of(
                new CanceledBooksRequest(1L, 1),
                new CanceledBooksRequest(2L, 2)
        );
    }

    @Test
    void 외부API_결제_승인_성공_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS, "test_payment_key_random", "test_order_number_nanoid", 29800, 0);

        PaymentApproveResponse response = new PaymentApproveResponse(
                "test_order_number_nanoid", 29800L, 0, PaymentStatusType.APPROVED.name(), OffsetDateTime.now());

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(request);

        assertNotNull(result);
        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;
        assertAll(
                () -> assertEquals(PaymentStatusType.APPROVED.name(), approveResult.status()),
                () -> assertEquals("test_order_number_nanoid", approveResult.orderNumber()),
                () -> assertEquals(29800L, approveResult.totalAmount())
        );

        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }

    @Test
    void 포인트_결제_승인_성공_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(PaymentMethodType.POINT, null,
                "test_order_number_nanoid", 0L, 29800);

        PaymentApproveResponse response = new PaymentApproveResponse("test_order_number_nanoid", 0L, 29800, "APPROVED",
                OffsetDateTime.now());

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(request);
        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;

        assertNotNull(result);
        assertAll(
                () -> assertEquals("APPROVED", approveResult.status()),
                () -> assertEquals("test_order_number_nanoid", approveResult.orderNumber()),
                () -> assertEquals(0L, approveResult.totalAmount())
        );
        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }

    @Test
    void 결제_승인_실패_및_저장() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS, "test_payment_key_random", "test_order_number_nanoid", 29800, null);

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenThrow(new RuntimeException("[500]SERVER:결제 실패"));

        PaymentResponse response = paymentService.approve(request);
        PaymentAbortedResponse aborted = (PaymentAbortedResponse) response;

        assertNotNull(response);
        assertAll(
                () -> assertEquals("결제 실패", aborted.message()),
                () -> assertNotNull(aborted.code())
        );

        verify(paymentErrorService, times(1)).saveAbortedPayment(any(), any());
        verify(eventPublisher, never()).publishPaymentApproved(any());
    }

    @Test
    void 포인트_결제_취소_성공_상태_변경_및_이벤트() {
        Payment payment = Payment.createApproved(
                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.POINT, 0L, 29800);

        when(paymentRepository.findByOrderNumber("test_order_number_nanoid")).thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        PaymentCancelRequest request = new PaymentCancelRequest(
                "test_order_number_nanoid", "고객 환불", 29800L, canceledBooks);

        PaymentCancelResponse result = paymentService.cancel(request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("고객 환불", result.cancelReason()),
                () -> assertEquals("test_order_number_nanoid", result.orderNumber()),
                () -> assertEquals(29800L, result.canceledAmount())
        );

        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(any(PaymentCancelResponse.class));
    }

//
//    @Test
//    void 외부API_결제_취소_성공_상태_변경_및_이벤트() {
//        Payment payment = Payment.createApproved(
//                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.TOSS, 29800L, null);
//
//        when(paymentRepository.findByOrderNumber("test_order_number_nanoid"))
//                .thenReturn(Optional.of(payment));
//
//        PaymentCancelRequest request = new PaymentCancelRequest(
//                "test_payment_key_random", "test_order_number_nanoid", "고객 변심", 29800L);
//
//        PaymentCancelResponse response = new PaymentCancelResponse(
//                "test_order_number_nanoid", "고객 변심", 29800L, "CANCELED", OffsetDateTime.now());
//
//        when(providerFactory.getProvider(any())).thenReturn(provider);
//        when(provider.cancel(any())).thenReturn(response);
//
//        PaymentCancelResponse result = paymentService.cancel(request);
//
//        assertNotNull(result);
//        assertAll(
//                () -> assertEquals("CANCELED", result.status()),
//                () -> assertEquals("고객 변심", result.cancelReason())
//        );
//
//        verify(providerFactory, times(1)).getProvider(any());
//        verify(provider, times(1)).cancel(any());
//        verify(eventPublisher, times(1)).publishPaymentCanceled(response);
//    }
//
//    @Test
//    void 결제_취소_실패() {
//        Payment payment = Payment.createApproved(
//                "test_order_number_nanoid", "test_payment_key_random", PaymentMethodType.TOSS, 29800L, null);
//        when(paymentRepository.findByOrderNumber("test_order_number_nanoid"))
//                .thenReturn(Optional.of(payment));
//
//        when(providerFactory.getProvider(any())).thenReturn(provider);
//        when(provider.cancel(any())).thenThrow(new RuntimeException("서버 오류"));
//
//        PaymentCancelRequest request = new PaymentCancelRequest(
//                "test_payment_key_random", "test_order_number_nanoid", "서버 오류", 29800L);
//
//        assertThatThrownBy(() -> paymentService.cancel(request))
//                .isInstanceOf(IllegalStateException.class)
//                .hasMessageContaining("결제 취소 실패");
//
//        verify(eventPublisher, never()).publishPaymentCanceled(any());
//    }
//
//    @Test
//    void 존재하지_않는_주문번호로_취소시_예외_발생() {
//        when(paymentRepository.findByOrderNumber("unknown")).thenReturn(Optional.empty());
//        PaymentCancelRequest request = new PaymentCancelRequest(
//                "test_payment_key_random", "unknown", "테스트", 29800L
//        );
//
//        assertThatThrownBy(() -> paymentService.cancel(request))
//                .isInstanceOf(NotFoundOrderNumberException.class)
//                .hasMessageContaining("해당 주문번호가 존재하지 않습니다.");
//    }
}
