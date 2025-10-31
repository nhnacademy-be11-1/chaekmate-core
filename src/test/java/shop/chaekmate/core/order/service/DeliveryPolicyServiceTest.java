package shop.chaekmate.core.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyDto;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyHistoryResponse;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyResponse;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.exception.DeliveryPolicyNotFoundException;
import shop.chaekmate.core.order.exception.DuplicatedDeliveryPolicyException;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DeliveryPolicyServiceTest {

    @Mock
    private DeliveryPolicyRepository deliveryPolicyRepository;

    @InjectMocks
    private DeliveryPolicyService deliveryPolicyService;

    DeliveryPolicy deliveryPolicy;

    @BeforeEach
    void setUp() {
        deliveryPolicy = new DeliveryPolicy(30000, 5000);
    }

    @Test
    void 배달_정책_등록_최초() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.empty());
        when(deliveryPolicyRepository.save(any(DeliveryPolicy.class))).thenReturn(deliveryPolicy);

        DeliveryPolicyResponse response = deliveryPolicyService.createPolicy(
                new DeliveryPolicyDto(deliveryPolicy.getFreeStandardAmount(), deliveryPolicy.getDeliveryFee()));

        assertNotNull(response);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(30000, response.freeStandardAmount()),
                () -> assertEquals(5000, response.deliveryFee())
        );

        verify(deliveryPolicyRepository, times(1)).findByDeletedAtIsNull();
        verify(deliveryPolicyRepository, times(1)).save(any(DeliveryPolicy.class));
    }

    @Test
    void 배달_정책_등록_중복() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicy newDeliveryPolicy = new DeliveryPolicy(30000, 5000);

        assertThrows(DuplicatedDeliveryPolicyException.class, () ->
                deliveryPolicyService.createPolicy(new DeliveryPolicyDto(newDeliveryPolicy.getFreeStandardAmount(), newDeliveryPolicy.getDeliveryFee()))
        );

        verify(deliveryPolicyRepository, times(1)).findByDeletedAtIsNull();
    }

    @Test
    void 배달_정책_등록_1개만_유지() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicy newDeliveryPolicy = new DeliveryPolicy(50000, 3000);
        when(deliveryPolicyRepository.save(any(DeliveryPolicy.class))).thenReturn(newDeliveryPolicy);

        DeliveryPolicyResponse response = deliveryPolicyService.createPolicy(
                new DeliveryPolicyDto(newDeliveryPolicy.getFreeStandardAmount(), newDeliveryPolicy.getDeliveryFee()));

        assertNotNull(response);
        assertAll(
                () -> assertEquals(50000, response.freeStandardAmount()),
                () -> assertEquals(3000, response.deliveryFee())
        );

        verify(deliveryPolicyRepository, times(1)).findByDeletedAtIsNull();
        verify(deliveryPolicyRepository, times(1)).deleteById(deliveryPolicy.getId());
        verify(deliveryPolicyRepository, times(1)).save(any(DeliveryPolicy.class));
    }

    @Test
    void 배달_정책_기록_조회_관리자() {
        Pageable pageable = PageRequest.of(0, 15);
        DeliveryPolicy newDeliveryPolicy = new DeliveryPolicy(50000, 3000);

        when(deliveryPolicyRepository.findAllByOrderByCreatedAtDesc(pageable))
                .thenReturn(new PageImpl<>(List.of(deliveryPolicy, newDeliveryPolicy), pageable, 2));

        Page<DeliveryPolicyHistoryResponse> response = deliveryPolicyService.getPolicyHistory(pageable);

        assertAll(
                () -> assertThat(response.getContent())
                        .hasSize(2)
                        .extracting(
                                DeliveryPolicyHistoryResponse::freeStandardAmount,
                                DeliveryPolicyHistoryResponse::deliveryFee
                        )
                        .containsExactlyInAnyOrder(
                                org.assertj.core.api.Assertions.tuple(30000, 5000),
                                org.assertj.core.api.Assertions.tuple(50000, 3000)
                        ),
                () -> assertEquals(0, response.getNumber()),
                () -> assertEquals(15, response.getSize()),
                () -> assertEquals(2, response.getTotalElements()),
                () -> assertEquals(1, response.getTotalPages())
        );

        verify(deliveryPolicyRepository, times(1)).findAllByOrderByCreatedAtDesc(pageable);
    }

    @Test
    void 현재_배달_정책_조회_성공() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponse response = deliveryPolicyService.getPolicy();

        assertNotNull(response);
        assertAll(
                () -> assertEquals(deliveryPolicy.getFreeStandardAmount(), response.freeStandardAmount()),
                () -> assertEquals(deliveryPolicy.getDeliveryFee(), response.deliveryFee())
        );

        verify(deliveryPolicyRepository, times(1)).findByDeletedAtIsNull();
    }

    @Test
    void 현재_배달_정책_조회_실패() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.empty());

        assertThrows(DeliveryPolicyNotFoundException.class, () -> deliveryPolicyService.getPolicy());

        verify(deliveryPolicyRepository, times(1)).findByDeletedAtIsNull();
    }
}