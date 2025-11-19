package shop.chaekmate.core.point.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

//테스트 작성
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PointPolicyServiceTest {

    @Mock
    private PointPolicyRepository pointPolicyRepository;

    @InjectMocks
    private PointService pointService;

    PointPolicy pointPolicy;

    @BeforeEach
    void setUp() {
        pointPolicy = new PointPolicy(PointEarnedType.ORDER, 100);
    }

    @Test
    void 포인트_정책_등록_성공() {
        // given
        CreatePointPolicyRequest request = new CreatePointPolicyRequest(PointEarnedType.ORDER, 100);
        when(pointPolicyRepository.existsByType(PointEarnedType.ORDER)).thenReturn(false);
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenReturn(pointPolicy);

        // when
        CreatePointPolicyResponse response = pointService.createPointPolicyRequest(request);
        
        // then
        assertNotNull(response);
        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(PointEarnedType.ORDER, response.pointEarnedType()),
                () -> assertEquals(100, response.point())
        );
        verify(pointPolicyRepository, times(1)).existsByType(PointEarnedType.ORDER);
        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void 포인트_정책_등록_중복이면_예외() {
        // given
        CreatePointPolicyRequest request = new CreatePointPolicyRequest(PointEarnedType.ORDER, 100);
        when(pointPolicyRepository.existsByType(PointEarnedType.ORDER)).thenReturn(true);

        // when & then
        assertThrows(DuplicatePointPolicyException.class, () -> pointService.createPointPolicyRequest(request));
        verify(pointPolicyRepository, times(1)).existsByType(PointEarnedType.ORDER);
        verify(pointPolicyRepository, times(0)).save(any(PointPolicy.class));
    }

    @Test
    void 포인트_정책_수정_성공() {
        // given
        UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(PointEarnedType.ORDER, 500);
        PointPolicy existing = new PointPolicy(PointEarnedType.ORDER, 400);
        when(pointPolicyRepository.findByType(PointEarnedType.ORDER)).thenReturn(Optional.of(existing));
        when(pointPolicyRepository.save(any(PointPolicy.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UpdatePointPolicyResponse response = pointService.updatePointPolicy(request);

        // then
        assertNotNull(response);
        assertAll(
                () -> assertEquals(PointEarnedType.ORDER, response.earnedType()),
                () -> assertEquals(500, response.point())
        );
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.ORDER);
        verify(pointPolicyRepository, times(1)).save(any(PointPolicy.class));
    }

    @Test
    void 포인트_정책_수정_실패_정책_없음() {
        // given
        UpdatePointPolicyRequest request = new UpdatePointPolicyRequest(PointEarnedType.IMAGE_REVIEW, 500);
        when(pointPolicyRepository.findByType(PointEarnedType.IMAGE_REVIEW)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PointPolicyNotFoundException.class, () -> pointService.updatePointPolicy(request));
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.IMAGE_REVIEW);
        verify(pointPolicyRepository, times(0)).save(any(PointPolicy.class));
    }

    @Test
    void 포인트_정책_조회_성공() {
        // given
        when(pointPolicyRepository.findByType(PointEarnedType.WELCOME)).thenReturn(Optional.of(pointPolicy));

        // when
        PointPolicyResponse response = pointService.getPolicyByType(PointEarnedType.WELCOME);

        // then
        assertNotNull(response);
        assertAll(
                () -> assertEquals(pointPolicy.getType(), response.earnType()),
                () -> assertEquals(pointPolicy.getPoint(), response.point())
        );
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.WELCOME);
    }

    @Test
    void 포인트_정책_조회_실패() {
        // given
        when(pointPolicyRepository.findByType(PointEarnedType.IMAGE_REVIEW)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PointPolicyNotFoundException.class, () -> pointService.getPolicyByType(PointEarnedType.IMAGE_REVIEW));
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.IMAGE_REVIEW);
    }

    @Test
    void 포인트_정책_삭제_성공() {
        // given
        DeletePointPolicyRequest request = new DeletePointPolicyRequest(PointEarnedType.ORDER);
        when(pointPolicyRepository.findByType(PointEarnedType.ORDER)).thenReturn(Optional.of(pointPolicy));

        // when
        pointService.deletePointPolicyResponse(request);

        // then
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.ORDER);
        verify(pointPolicyRepository, times(1)).delete(pointPolicy);
    }

    @Test
    void 포인트_정책_삭제_실패_정책_없음() {
        // given
        DeletePointPolicyRequest request = new DeletePointPolicyRequest(PointEarnedType.IMAGE_REVIEW);
        when(pointPolicyRepository.findByType(PointEarnedType.IMAGE_REVIEW)).thenReturn(Optional.empty());

        // when & then
        assertThrows(PointPolicyNotFoundException.class, () -> pointService.deletePointPolicyResponse(request));
        verify(pointPolicyRepository, times(1)).findByType(PointEarnedType.IMAGE_REVIEW);
        verify(pointPolicyRepository, times(0)).delete(any(PointPolicy.class));
    }

    
}
