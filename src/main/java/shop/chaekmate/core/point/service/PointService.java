package shop.chaekmate.core.point.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.DeletePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.response.CreatePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.DeletePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.ReadPointPolicyResponse;
import shop.chaekmate.core.point.dto.response.UpdatePointPolicyResponse;
import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.exception.DuplicatePolicyException;
import shop.chaekmate.core.point.repository.PointPolicyRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointPolicyRepository pointPolicyRepository;

    //포인트 규정 등록 기능 구현
    @Transactional
    public CreatePointPolicyResponse createPointPolicyRequest(CreatePointPolicyRequest request) {
        if (pointPolicyRepository.existsByType(request.earnedType())) {
            throw new DuplicatePolicyException("Policy already exists: " + request.earnedType());
        }

        try {
            PointPolicy policy = new PointPolicy(request.earnedType(), request.point());
            pointPolicyRepository.save(policy);
            return new CreatePointPolicyResponse(policy.getId(), policy.getType(), policy.getPoint());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePolicyException("Policy already exists: " + e);
        }
    }

    //포인트 규정 수정 기능
    @Transactional
    public UpdatePointPolicyResponse updatePointPolicy(UpdatePointPolicyRequest request) {
        PointPolicy policy = pointPolicyRepository.findByType(request.type())
                .orElseThrow(() -> new NoSuchElementException("PointPolicy not found type=" + request.type()));

        policy.updatePointPolicy(request.type(), request.point());
        PointPolicy saved = pointPolicyRepository.save(policy);
        return new UpdatePointPolicyResponse(saved.getId(), saved.getType(), saved.getPoint());
    }

    // 추가: 단건 조회
    public ReadPointPolicyResponse getPolicyByType(shop.chaekmate.core.point.entity.type.PointEarnedType type) {
        PointPolicy policy = pointPolicyRepository.findByType(type)
                .orElseThrow(() -> new NoSuchElementException("PointPolicy not found type=" + type));
        return new ReadPointPolicyResponse(policy.getId(), policy.getType(), policy.getPoint());
    }

    //삭제 기능
    @Transactional
    public void deletePointPolicyResponse(DeletePointPolicyRequest request) {
        PointPolicy policy = pointPolicyRepository.findByType(request.pointEarnedType())
                .orElseThrow(() -> new NoSuchElementException("PointPolicy not found type=" + request.pointEarnedType()));
        pointPolicyRepository.delete(policy);


    }
    
}
