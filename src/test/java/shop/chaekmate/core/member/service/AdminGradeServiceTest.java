package shop.chaekmate.core.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.exception.GradeNotFoundException;
import shop.chaekmate.core.member.repository.GradeRepository;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AdminGradeServiceTest {

    @Mock
    GradeRepository gradeRepository;

    @InjectMocks
    AdminGradeService adminGradeService;

    @Test
    void 등급_생성_성공() {
        CreateGradeRequest req = new CreateGradeRequest("브론즈", (byte) 1, 0);

        given(gradeRepository.save(any(Grade.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        adminGradeService.createGrade(req);

        ArgumentCaptor<Grade> captor = ArgumentCaptor.forClass(Grade.class);
        verify(gradeRepository).save(captor.capture());

        Grade saved = captor.getValue();

        assertThat(saved.getName()).isEqualTo("브론즈");
        assertThat(saved.getPointRate()).isEqualTo((byte) 1);
        assertThat(saved.getUpgradeStandardAmount()).isZero();
    }

    @Test
    void 등급_수정_성공() {
        Long gradeId = 1L;

        Grade grade = spy(new Grade("브론즈", (byte) 1, 100));
        UpdateGradeRequest req = new UpdateGradeRequest("실버", (byte) 3, 10000);

        given(gradeRepository.findById(gradeId)).willReturn(Optional.of(grade));

        adminGradeService.updateGrade(gradeId, req);

        verify(grade).update("실버", (byte) 3, 10000);

        assertThat(grade.getName()).isEqualTo("실버");
        assertThat(grade.getPointRate()).isEqualTo((byte) 3);
        assertThat(grade.getUpgradeStandardAmount()).isEqualTo(10000);
    }

    @Test
    void 등급_수정_실패_등급없음() {
        Long gradeId = 1L;

        given(gradeRepository.findById(gradeId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> adminGradeService.updateGrade(gradeId,
                new UpdateGradeRequest("실버", (byte) 3, 10000)))
                .isInstanceOf(GradeNotFoundException.class);

        verify(gradeRepository, never()).save(any());
    }

    @Test
    void 등급_삭제_성공() {
        Long gradeId = 1L;

        Grade grade = spy(new Grade("브론즈", (byte) 1, 100));

        given(gradeRepository.findById(gradeId)).willReturn(Optional.of(grade));

        adminGradeService.deleteGrade(gradeId);

        verify(gradeRepository).delete(grade);
    }
}
