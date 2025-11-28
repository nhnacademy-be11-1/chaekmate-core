package shop.chaekmate.core.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.order.dto.request.DeliveryPolicyDto;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyHistoryResponse;
import shop.chaekmate.core.order.dto.response.DeliveryPolicyResponse;
import shop.chaekmate.core.order.entity.DeliveryPolicy;
import shop.chaekmate.core.order.exception.NotFoundDeliveryPolicyException;
import shop.chaekmate.core.order.exception.DuplicatedDeliveryPolicyException;
import shop.chaekmate.core.order.repository.DeliveryPolicyRepository;

@Service
@RequiredArgsConstructor
public class DeliveryPolicyService {

    private final DeliveryPolicyRepository deliveryPolicyRepository;

    // 정책 등록 및 기존 정책 자동 삭제
    @Transactional
    public DeliveryPolicyResponse createPolicy(DeliveryPolicyDto dto) {
        deliveryPolicyRepository.findByDeletedAtIsNull()
                .ifPresent(policy -> {
                    if (policy.equalsDeliveryPolicy(dto.freeStandardAmount(), dto.deliveryFee())) {
                        throw new DuplicatedDeliveryPolicyException();
                    }
                    deliveryPolicyRepository.deleteById(policy.getId());
                });

        DeliveryPolicy newPolicy = deliveryPolicyRepository.save(
                new DeliveryPolicy(dto.freeStandardAmount(), dto.deliveryFee()));

        return new DeliveryPolicyResponse(newPolicy.getId(), newPolicy.getFreeStandardAmount(),
                newPolicy.getDeliveryFee());
    }

    // 정책 내역 전체 조회
    @Transactional(readOnly = true)
    public Page<DeliveryPolicyHistoryResponse> getPolicyHistory(Pageable pageable) {
        return deliveryPolicyRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(policy -> new DeliveryPolicyHistoryResponse(
                        policy.getId(),
                        policy.getFreeStandardAmount(),
                        policy.getDeliveryFee(),
                        policy.getCreatedAt(),
                        policy.getDeletedAt()
                ));
    }

    // 현재 정책 조회
    @Transactional(readOnly = true)
    public DeliveryPolicyResponse getPolicy() {
        DeliveryPolicy policy = deliveryPolicyRepository.findByDeletedAtIsNull()
                .orElseThrow(NotFoundDeliveryPolicyException::new);

        return new DeliveryPolicyResponse(policy.getId(), policy.getFreeStandardAmount(), policy.getDeliveryFee());
    }
}
