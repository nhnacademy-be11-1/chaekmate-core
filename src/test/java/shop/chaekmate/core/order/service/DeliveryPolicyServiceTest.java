package shop.chaekmate.core.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

        assertThat(response.freeStandardAmount()).isEqualTo(30000);
        assertThat(response.deliveryFee()).isEqualTo(5000);

        verify(deliveryPolicyRepository).save(any(DeliveryPolicy.class));
    }

    @Test
    void 배달_정책_등록_1개만_유지() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicy newDeliveryPolicy = new DeliveryPolicy(50000, 3000);
        when(deliveryPolicyRepository.save(any(DeliveryPolicy.class))).thenReturn(newDeliveryPolicy);

        DeliveryPolicyResponse response = deliveryPolicyService.createPolicy(
                new DeliveryPolicyDto(newDeliveryPolicy.getFreeStandardAmount(), newDeliveryPolicy.getDeliveryFee()));

        assertThat(response.freeStandardAmount()).isEqualTo(50000);
        assertThat(response.deliveryFee()).isEqualTo(3000);

        verify(deliveryPolicyRepository).deleteById(deliveryPolicy.getId());
        verify(deliveryPolicyRepository).save(any(DeliveryPolicy.class));
    }

    @Test
    void 배달_정책_기록_조회_관리자() {
        Pageable pageable = PageRequest.of(0, 15);
        DeliveryPolicy newDeliveryPolicy = new DeliveryPolicy(50000, 3000);

        when(deliveryPolicyRepository.findAllPolicy(pageable))
                .thenReturn(new PageImpl<>(List.of(deliveryPolicy, newDeliveryPolicy), pageable, 2));

        Page<DeliveryPolicyHistoryResponse> response = deliveryPolicyService.getPolicyHistory(pageable);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).freeStandardAmount()).isEqualTo(30000);
        assertThat(response.getContent().get(1).deliveryFee()).isEqualTo(3000);

        assertThat(response.getNumber()).isZero();
        assertThat(response.getSize()).isEqualTo(15);
        assertThat(response.getTotalElements()).isEqualTo(2);
        assertThat(response.getTotalPages()).isEqualTo(1);

    }

    @Test
    void 현재_배달_정책_조회_성공() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.of(deliveryPolicy));

        DeliveryPolicyResponse response = deliveryPolicyService.getPolicy();

        assertThat(response.freeStandardAmount()).isEqualTo(deliveryPolicy.getFreeStandardAmount());
        assertThat(response.deliveryFee()).isEqualTo(deliveryPolicy.getDeliveryFee());

        verify(deliveryPolicyRepository).findByDeletedAtIsNull();
    }

    @Test
    void 현재_배달_정책_조회_실패() {
        when(deliveryPolicyRepository.findByDeletedAtIsNull()).thenReturn(Optional.empty());

        assertThrows(DeliveryPolicyNotFoundException.class, () -> deliveryPolicyService.getPolicy());

        verify(deliveryPolicyRepository).findByDeletedAtIsNull();
    }
}