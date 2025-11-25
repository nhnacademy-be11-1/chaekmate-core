package shop.chaekmate.core.payment.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
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
import shop.chaekmate.core.payment.exception.AlreadyCanceledException;
import shop.chaekmate.core.payment.exception.ExceedCancelAmountException;
import shop.chaekmate.core.payment.exception.InvalidCancelAmountException;
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

    Long memberId;
    Long guestId;
    private String paymentKey;
    private String orderNumber;

    @BeforeEach
    void setUp() {
        canceledBooks = List.of(
                new CanceledBooksRequest(1L, 1),
                new CanceledBooksRequest(2L, 2)
        );
        memberId = 1L;
        guestId = null;
        paymentKey = "test_payment_key_random";
        orderNumber = "test_order_number_nanoid";
    }

    @Test
    void 회원_외부API_결제_승인_성공() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS,
                paymentKey,
                orderNumber,
                29800L,
                0);

        PaymentApproveResponse response = new PaymentApproveResponse(
                orderNumber,
                29800L,
                0,
                PaymentStatusType.APPROVED.name(),
                LocalDateTime.now());

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(memberId, request);
        assertNotNull(result);

        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;
        assertAll(
                () -> assertEquals(orderNumber, approveResult.orderNumber()),
                () -> assertEquals("APPROVED", approveResult.status()),
                () -> assertEquals(29800L, approveResult.totalAmount())
        );

        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }

    @Test
    void 회원_포인트_결제_승인_성공_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.POINT,
                null,
                orderNumber,
                0L,
                29800
        );

        PaymentApproveResponse response = new PaymentApproveResponse(
                orderNumber,
                0L,
                29800,
                "APPROVED",
                LocalDateTime.now()
        );

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(memberId, request);
        assertNotNull(result);

        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;

        assertAll(
                () -> assertEquals("APPROVED", approveResult.status()),
                () -> assertEquals(orderNumber, approveResult.orderNumber()),
                () -> assertEquals(0L, approveResult.totalAmount())
        );

        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }

    @Test
    void 회원_혼합_결제_승인_성공() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS,
                paymentKey,
                orderNumber,
                20000L,
                10000
        );

        PaymentApproveResponse response = new PaymentApproveResponse(
                orderNumber,
                20000L,
                10000,
                PaymentStatusType.APPROVED.name(),
                LocalDateTime.now()
        );

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(memberId, request);

        assertNotNull(result);

        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;

        assertAll(
                () -> assertEquals(orderNumber, approveResult.orderNumber()),
                () -> assertEquals("APPROVED", approveResult.status()),
                () -> assertEquals(20000L, approveResult.totalAmount()),
                () -> assertEquals(10000, approveResult.pointUsed())
        );

        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }


    @Test
    void 회원_포인트_결제_취소_성공_상태_변경_및_이벤트() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.POINT,
                0L,
                29800);

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.save(any(PaymentHistory.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey, orderNumber, "고객 환불", 29800L, canceledBooks);

        PaymentCancelResponse result = paymentService.cancel(memberId, request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("고객 환불", result.cancelReason()),
                () -> assertEquals("test_order_number_nanoid", result.orderNumber()),
                () -> assertEquals(29800L, result.canceledAmount()),
                () -> assertEquals(PaymentStatusType.CANCELED, payment.getPaymentStatus()),
                () -> assertEquals(0L, payment.getTotalAmount()),
                () -> assertEquals(0, payment.getPointUsed())
        );

        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(any(PaymentCancelResponse.class));
    }

    @Test
    void 회원_부분_결제_취소_성공_상태_변경_및_이벤트() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                29800L,
                0
        );

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.save(any(PaymentHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "고객 요청에 의한 부분 환불",
                15000L,
                canceledBooks
        );

        PaymentCancelResponse result = paymentService.cancel(memberId, request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(orderNumber, result.orderNumber()),
                () -> assertEquals("고객 요청에 의한 부분 환불", result.cancelReason()),
                () -> assertEquals(15000L, result.canceledAmount()),
                () -> assertEquals(PaymentStatusType.PARTIAL_CANCELED, payment.getPaymentStatus()),
                () -> assertEquals(14800L, payment.getTotalAmount()) // 29800 - 15000
        );

        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(any(PaymentCancelResponse.class));
    }


    @Test
    void 회원_혼합_결제_취소_성공_상태_변경_및_이벤트() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                20000L,
                10000
        );

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.save(any(PaymentHistory.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "전체 취소 요청",
                null,
                canceledBooks
        );

        PaymentCancelResponse result = paymentService.cancel(memberId, request);

        assertNotNull(result);
        assertAll(
                () -> assertEquals(orderNumber, result.orderNumber()),
                () -> assertEquals("전체 취소 요청", result.cancelReason()),
                () -> assertEquals(30000L, result.canceledAmount()),
                () -> assertEquals(PaymentStatusType.CANCELED, payment.getPaymentStatus())
        );

        verify(paymentHistoryRepository, times(1)).save(any(PaymentHistory.class));
        verify(eventPublisher, times(1)).publishPaymentCanceled(any(PaymentCancelResponse.class));
    }

    @Test
    void 비회원_외부API_결제_승인_성공_이벤트() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS,
                paymentKey,
                orderNumber,
                29800,
                0
        );

        PaymentApproveResponse response = new PaymentApproveResponse(
                orderNumber,
                29800L,
                0,
                PaymentStatusType.APPROVED.name(),
                LocalDateTime.now()
        );

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenReturn(response);

        PaymentResponse result = paymentService.approve(guestId, request);
        PaymentApproveResponse approveResult = (PaymentApproveResponse) result;

        assertNotNull(result);
        assertAll(
                () -> assertEquals("APPROVED", approveResult.status()),
                () -> assertEquals(orderNumber, approveResult.orderNumber()),
                () -> assertEquals(29800L, approveResult.totalAmount())
        );

        verify(providerFactory, times(1)).getProvider(any());
        verify(provider, times(1)).approve(any());
        verify(eventPublisher, times(1)).publishPaymentApproved(response);
    }



    @Test
    void 결제_승인_실패_및_저장() {
        PaymentApproveRequest request = new PaymentApproveRequest(
                PaymentMethodType.TOSS,
                paymentKey,
                orderNumber,
                29800,
                0
        );

        when(providerFactory.getProvider(any())).thenReturn(provider);
        when(provider.approve(any())).thenThrow(new RuntimeException("[500]SERVER:결제 실패"));

        PaymentResponse result = paymentService.approve(memberId, request);
        assertNotNull(result);

        PaymentAbortedResponse abortedResult = (PaymentAbortedResponse) result;

        assertAll(
                () -> assertEquals("결제 실패", abortedResult.message()),
                () -> assertNotNull(abortedResult.code())
        );

        verify(paymentErrorService, times(1)).saveAbortedPayment(any(), any());
        verify(eventPublisher, never()).publishPaymentApproved(any());
    }

    @Test
    void 존재하지_않는_주문번호로_취소시_예외_발생() {
        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.empty());

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "주문번호를 찾을 수 없음",
                10000L,
                canceledBooks
        );

        assertThrows(NotFoundOrderNumberException.class, () -> paymentService.cancel(memberId, request));

        verify(paymentRepository, times(1)).findByOrderNumber(orderNumber);
        verify(paymentHistoryRepository, never()).save(any());
        verify(eventPublisher, never()).publishPaymentCanceled(any());
    }

    @Test
    void 결제_취소_실패_잘못된_금액() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                10000L,
                0
        );

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "잘못된 금액 테스트",
                0L,
                canceledBooks
        );

        assertThrows(InvalidCancelAmountException.class, () -> paymentService.cancel(memberId, request));

        verify(paymentHistoryRepository, never()).save(any());
        verify(eventPublisher, never()).publishPaymentCanceled(any());
    }

    @Test
    void 결제_취소_실패_취소금액_결제금액초과() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                10000L,
                5000
        );

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "금액 초과 테스트",
                20000L,
                canceledBooks
        );

        assertThrows(ExceedCancelAmountException.class, () -> paymentService.cancel(memberId, request));

        verify(paymentHistoryRepository, never()).save(any());
        verify(eventPublisher, never()).publishPaymentCanceled(any());
    }

    @Test
    void 결제_취소_실패_이미_취소된_결제() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                10000L,
                0
        );

        payment.cancelOrPartial(10000L); // 이미 취소됨

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));

        PaymentCancelRequest request = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "이미 취소된 결제 재시도 테스트",
                10000L,
                canceledBooks
        );

        assertThrows(AlreadyCanceledException.class, () -> paymentService.cancel(memberId, request));

        verify(paymentHistoryRepository, never()).save(any());
        verify(eventPublisher, never()).publishPaymentCanceled(any());
    }

    @Test
    void 마지막_부분취소시_전체_취소_상태_변경() {
        Payment payment = Payment.createApproved(
                orderNumber,
                paymentKey,
                PaymentMethodType.TOSS,
                20000L,
                0
        );

        when(paymentRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(payment));
        when(paymentHistoryRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // 1차 부분 취소 10000
        PaymentCancelRequest firstCancel = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "1차 부분취소",
                15000L,
                canceledBooks
        );

        PaymentCancelResponse firstResult = paymentService.cancel(memberId, firstCancel);

        assertNotNull(firstResult);
        assertAll(
                () -> assertEquals(orderNumber, firstResult.orderNumber()),
                () -> assertEquals("1차 부분취소", firstResult.cancelReason()),
                () -> assertEquals(15000L, firstResult.canceledAmount()),
                () -> assertEquals(PaymentStatusType.PARTIAL_CANCELED, payment.getPaymentStatus()),
                () -> assertEquals(5000L, payment.getTotalAmount())
        );

        // 2차 부분 취소 -> 전체취소
        PaymentCancelRequest secondCancel = new PaymentCancelRequest(
                paymentKey,
                orderNumber,
                "2차 부분취소",
                5000L,
                canceledBooks
        );

        PaymentCancelResponse secondResult = paymentService.cancel(memberId, secondCancel);

        assertNotNull(secondResult);
        assertAll(
                () -> assertEquals(orderNumber, secondResult.orderNumber()),
                () -> assertEquals("2차 부분취소", secondResult.cancelReason()),
                () -> assertEquals(5000L, secondResult.canceledAmount()),
                () -> assertEquals(PaymentStatusType.CANCELED, payment.getPaymentStatus()),
                () -> assertEquals(0L, payment.getTotalAmount())
        );

        verify(paymentHistoryRepository, times(2)).save(any());
        verify(eventPublisher, times(2)).publishPaymentCanceled(any(PaymentCancelResponse.class));
    }

    //비회원 취소 로직 고민해야 함 guestId = null
    // 취소 시 반환 값 포인트 비회원x
    // 1.api 취소,
    // 2.회원가입 후 취소 포인트 반환,
    // 3.취소 x
}
