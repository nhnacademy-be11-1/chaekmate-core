package shop.chaekmate.core.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.payment.dto.PaymentHistoryDto;
import shop.chaekmate.core.payment.entity.type.PaymentMethodType;
import shop.chaekmate.core.payment.entity.type.PaymentStatusType;
import shop.chaekmate.core.payment.repository.PaymentHistoryRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentHistoryServiceTest {

    @Mock
    private PaymentHistoryRepository paymentHistoryRepository;

    @InjectMocks
    private PaymentHistoryService paymentHistoryService;

    Pageable pageable;
    PaymentHistoryDto tossApproved;
    PaymentHistoryDto tossCanceled;
    PaymentHistoryDto pointApproved;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void setUp() {
        pageable = PageRequest.of(0, 10, Sort.by("occurredAt").descending());
        start = LocalDateTime.of(2025, 11, 1, 0, 0);
        end = LocalDateTime.of(2025, 11, 10, 23, 59);

        var offset = java.time.ZoneOffset.ofHours(9);

        tossApproved = new PaymentHistoryDto(
                "ORDER_NUMBER1",
                PaymentMethodType.TOSS,
                PaymentStatusType.APPROVED,
                12000L,
                null,
                OffsetDateTime.of(2025, 11, 3, 10, 0, 0, 0, offset)
        );
        tossCanceled = new PaymentHistoryDto(
                "ORDER_NUMBER2",
                PaymentMethodType.TOSS,
                PaymentStatusType.CANCELED,
                15000L,
                "사용자 취소",
                java.time.OffsetDateTime.of(2025, 11, 6, 15, 0, 0, 0, offset)
        );
        pointApproved = new PaymentHistoryDto(
                "ORDER_NUMBER3",
                PaymentMethodType.POINT,
                PaymentStatusType.APPROVED,
                20000L,
                null,
                java.time.OffsetDateTime.of(2025, 11, 4, 18, 0, 0, 0, offset)
        );
    }

    @Test
    void 전체_결제_내역_조회() {
        Page<PaymentHistoryDto> mockPage =
                new PageImpl<>(List.of(tossApproved, tossCanceled, pointApproved), pageable, 3);
        when(paymentHistoryRepository.findHistoriesByFilter(null, null, null, pageable))
                .thenReturn(mockPage);

        Page<PaymentHistoryDto> result =
                paymentHistoryService.findHistoriesByFilter(null, null, null, pageable);

        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.getContent()).hasSize(3),
                () -> assertThat(result.getContent())
                        .extracting(PaymentHistoryDto::paymentType)
                        .containsExactlyInAnyOrder(
                                PaymentMethodType.TOSS,
                                PaymentMethodType.TOSS,
                                PaymentMethodType.POINT
                        )
        );

        verify(paymentHistoryRepository, times(1))
                .findHistoriesByFilter(null, null, null, pageable);
    }


    @Test
    void 결제수단별_전체_조회() {
        Page<PaymentHistoryDto> mockPage = new PageImpl<>(List.of(tossApproved, tossCanceled), pageable, 2);
        when(paymentHistoryRepository.findHistoriesByFilter(PaymentMethodType.TOSS, null, null, pageable)).thenReturn(
                mockPage);

        Page<PaymentHistoryDto> result =
                paymentHistoryService.findHistoriesByFilter(PaymentMethodType.TOSS, null, null, pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getContent())
                        .allMatch(dto -> dto.paymentType() == PaymentMethodType.TOSS)
        );

        verify(paymentHistoryRepository, times(1))
                .findHistoriesByFilter(PaymentMethodType.TOSS, null, null, pageable);
    }

    @Test
    void 모든_결제수단_기간별_조회() {
        Page<PaymentHistoryDto> mockPage = new PageImpl<>(List.of(tossApproved, pointApproved), pageable, 2);
        when(paymentHistoryRepository.findHistoriesByFilter(null, start, end, pageable)).thenReturn(mockPage);

        Page<PaymentHistoryDto> result = paymentHistoryService.findHistoriesByFilter(null, start, end, pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getContent())
                        .extracting(PaymentHistoryDto::paymentType)
                        .containsExactlyInAnyOrder(PaymentMethodType.TOSS, PaymentMethodType.POINT),
                () -> assertThat(result.getContent())
                        .allMatch(dto -> dto.occurredAt().toLocalDateTime().isAfter(start.minusSeconds(1)) &&
                                dto.occurredAt().toLocalDateTime().isBefore(end.plusSeconds(1)))
        );

        verify(paymentHistoryRepository, times(1))
                .findHistoriesByFilter(null, start, end, pageable);
    }

    @Test
    void 결제수단_기간별_조회() {
        Page<PaymentHistoryDto> mockPage = new PageImpl<>(List.of(tossApproved, tossCanceled), pageable, 2);
        when(paymentHistoryRepository.findHistoriesByFilter(PaymentMethodType.TOSS, start, end, pageable))
                .thenReturn(mockPage);

        Page<PaymentHistoryDto> result =
                paymentHistoryService.findHistoriesByFilter(PaymentMethodType.TOSS, start, end, pageable);

        assertAll(
                () -> assertThat(result.getContent()).hasSize(2),
                () -> assertThat(result.getContent())
                        .allMatch(dto -> dto.paymentType() == PaymentMethodType.TOSS),
                () -> assertThat(result.getContent())
                        .allMatch(dto -> dto.occurredAt().toLocalDateTime().isAfter(start.minusSeconds(1)) &&
                                dto.occurredAt().toLocalDateTime().isBefore(end.plusSeconds(1)))
        );

        verify(paymentHistoryRepository, times(1))
                .findHistoriesByFilter(PaymentMethodType.TOSS, start, end, pageable);
    }
}
