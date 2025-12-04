package shop.chaekmate.core.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.chaekmate.core.member.dto.request.CreateGradeRequest;
import shop.chaekmate.core.member.dto.request.UpdateGradeRequest;
import shop.chaekmate.core.member.entity.Grade;
import shop.chaekmate.core.member.exception.BaseGradeNotAllowedException;
import shop.chaekmate.core.member.exception.DuplicatedGradeStandardAmountException;
import shop.chaekmate.core.member.exception.GradeNotFoundException;
import shop.chaekmate.core.member.repository.GradeRepository;

@Service
@RequiredArgsConstructor
public class AdminGradeService {
    private final GradeRepository gradeRepository;

    @Transactional
    public void updateGrade(Long gradeId, UpdateGradeRequest request) {
        Grade grade = gradeRepository.findById(gradeId).orElseThrow(GradeNotFoundException::new);

        int currentAmount = grade.getUpgradeStandardAmount();
        int newAmount = request.upgradeStandardAmount();

        if (currentAmount == 0 && newAmount != 0) {
            throw new BaseGradeNotAllowedException();
        }

        // 등급 수정 시 자기 자신 제외하고 upgradeStandardAmount 중복 방지
        if (Boolean.TRUE.equals(gradeRepository.existsByUpgradeStandardAmountAndIdNot(gradeId, request.upgradeStandardAmount()))){
            throw new DuplicatedGradeStandardAmountException();
        }
        grade.update(request.name(), request.pointRate(), request.upgradeStandardAmount());
    }

    @Transactional
    public void createGrade(CreateGradeRequest request) {
        int upgradeStandardAmount = request.upgradeStandardAmount();

        // 동일한 upgradeStandardAmount를 가진 등급이 이미 존재하면 예외
        if (Boolean.TRUE.equals(gradeRepository.existsByUpgradeStandardAmount(upgradeStandardAmount))){
            throw new DuplicatedGradeStandardAmountException();
        }

        Grade grade = new Grade(request.name(), request.pointRate(), request.upgradeStandardAmount());
        gradeRepository.save(grade);
    }

    @Transactional
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(GradeNotFoundException::new);
        int upgradeStandardAmount = grade.getUpgradeStandardAmount();

        // 기준금액이 0인 등급은 삭제 금지
        if (upgradeStandardAmount == 0) {
            throw new BaseGradeNotAllowedException();
        }
        gradeRepository.delete(grade);
    }
}
