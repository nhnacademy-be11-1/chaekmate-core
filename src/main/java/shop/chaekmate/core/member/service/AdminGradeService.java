package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.exception.GradeNotFoundException;
import shop.chaekmate.core.member.repository.GradeRepository;

@Service
@RequiredArgsConstructor
public class AdminGradeService {
    private final GradeRepository gradeRepository;

    @Transactional
    public void updateGrade(Long gradeId, UpdateGradeRequest request) {
        Grade grade = gradeRepository.findById(gradeId).orElseThrow(GradeNotFoundException::new);
        grade.update(request.name(), request.pointRate(), request.upgradeStandardAmount());
    }

    @Transactional
    public void createGrade(CreateGradeRequest request) {
        Grade grade = new Grade(request.name(), request.pointRate(), request.upgradeStandardAmount());
        gradeRepository.save(grade);
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        gradeRepository.deleteById(gradeId);
    }
}
