package shop.chaekmate.core.point.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.point.dto.request.CreatePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.DeletePointPolicyRequest;
import shop.chaekmate.core.point.dto.request.UpdatePointPolicyRequest;
import shop.chaekmate.core.point.dto.response.CreatePointPolicyResponse;
import shop.chaekmate.core.point.dto.response.PointPolicyResponse;
import shop.chaekmate.core.point.dto.response.UpdatePointPolicyResponse;
import shop.chaekmate.core.point.entity.PointPolicy;
import shop.chaekmate.core.point.entity.type.PointEarnedType;
import shop.chaekmate.core.point.exception.DuplicatePointPolicyException;
import shop.chaekmate.core.point.exception.PointPolicyNotFoundException;
import shop.chaekmate.core.point.repository.PointPolicyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointPolicyRepository pointPolicyRepository;

    //포인트 규정 등록 기능 구현
    @Transactional
    public CreatePointPolicyResponse createPointPolicyRequest(CreatePointPolicyRequest request) {
        if (pointPolicyRepository.existsByType(request.earnedType())) {
            throw new DuplicatePointPolicyException();
        }

        try {
            PointPolicy policy = new PointPolicy(request.earnedType(), request.point());
            pointPolicyRepository.save(policy);
            return new CreatePointPolicyResponse(policy.getId(), policy.getType(), policy.getPoint());
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePointPolicyException();
        }
    }

    //포인트 규정 수정 기능
    @Transactional(readOnly = true)
    public UpdatePointPolicyResponse updatePointPolicy(UpdatePointPolicyRequest request) {
        PointPolicy policy = pointPolicyRepository.findByType(request.type())
                .orElseThrow(PointPolicyNotFoundException::new);

        policy.updatePointPolicy(request.type(), request.point());
        PointPolicy saved = pointPolicyRepository.save(policy);
        return new UpdatePointPolicyResponse(saved.getId(), saved.getType(), saved.getPoint());
    }

    //정책 단건 조회
    @Transactional(readOnly = true)
    public PointPolicyResponse getPolicyByType(PointEarnedType type) {
        PointPolicy policy = pointPolicyRepository.findByType(type)
                .orElseThrow(PointPolicyNotFoundException::new);
        return new PointPolicyResponse(policy.getId(), policy.getType(), policy.getPoint());
    }

    //정책 전체 조회
    @Transactional(readOnly = true)
    public List<PointPolicyResponse> getAllPolicies() {
        return pointPolicyRepository.findAll()
                .stream()
                .map(policy -> new PointPolicyResponse(
                        policy.getId(),
                        policy.getType(),
                        policy.getPoint()
                ))
                .toList();
    }

    //삭제 기능
    @Transactional(readOnly = true)
    public void deletePointPolicyResponse(DeletePointPolicyRequest request) {
        PointPolicy policy = pointPolicyRepository.findByType(request.pointEarnedType())
                .orElseThrow(PointPolicyNotFoundException::new);
        pointPolicyRepository.delete(policy);
    }
    
}
