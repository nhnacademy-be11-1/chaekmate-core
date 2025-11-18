package shop.chaekmate.core.point.service;

import org.springframework.transaction.annotation.Transactional;import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.member.entity.Member;
import shop.chaekmate.core.member.repository.MemberRepository;
import shop.chaekmate.core.point.dto.request.CreatePointHistoryRequest;
import shop.chaekmate.core.point.dto.response.CreatePointHistoryResponse;
import shop.chaekmate.core.point.dto.response.MemberPointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointHistoryResponse;
import shop.chaekmate.core.point.dto.response.PointResponse;
import shop.chaekmate.core.point.entity.PointHistory;
import shop.chaekmate.core.point.entity.type.PointSpendType;
import shop.chaekmate.core.point.exception.MemberNotFoundException;
import shop.chaekmate.core.point.repository.PointHistoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    //회원 포인트 적립
    @Transactional
    public CreatePointHistoryResponse earnPointHistory(Long memberId, CreatePointHistoryRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        PointHistory history = new PointHistory(
                member,
                PointSpendType.EARN,
                request.point(),
                request.source()
        );

        PointHistory saved = pointHistoryRepository.save(history);

        return new CreatePointHistoryResponse(
                saved.getId(),
                saved.getMember().getId(),
                saved.getType(),
                saved.getPoint(),
                saved.getSource()
        );
    }

    //회원 포인트 차감
    @Transactional
    public CreatePointHistoryResponse spendPointHistory(Long memberId, CreatePointHistoryRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);


        PointHistory history = new PointHistory(
                member,
                PointSpendType.SPEND,
                request.point(),
                request.source()
        );

        PointHistory saved = pointHistoryRepository.save(history);

        return new CreatePointHistoryResponse(
                saved.getId(),
                saved.getMember().getId(),
                saved.getType(),
                saved.getPoint(),
                saved.getSource()
        );
    }


    //포인트 history 조회 (전체)
    @Transactional(readOnly = true)
    public Page<PointHistoryResponse> getPointHistory(Pageable pageable) {
        return pointHistoryRepository.findAll(pageable)
                .map(point -> new PointHistoryResponse(
                        point.getId(),
                        point.getMember().getId(),
                        point.getType(),
                        point.getPoint(),
                        point.getSource(),
                        point.getCreatedAt()
                        )
                );
    }

    //포인트 history 조회 (특정 회원)
    @Transactional(readOnly = true)
    public Page<MemberPointHistoryResponse> getPointHistoryByMemberId(Long memberId, Pageable pageable) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        return pointHistoryRepository.findByMemberId(memberId, pageable)
                .map(point -> new MemberPointHistoryResponse(
                        point.getMember().getId(),
                        point.getType(),
                        point.getPoint(),
                        point.getSource(),
                        point.getCreatedAt())
                );
    }

    //회원 포인트 조회
    @Transactional
    public PointResponse readPointResponse(Long memberId) {
        memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        List<PointHistory> histories = pointHistoryRepository.findByMemberId(memberId);

        int earnedPoint = histories.stream()
                .filter(h -> h.getType() == PointSpendType.EARN)
                .mapToInt(PointHistory::getPoint)
                .sum();

        int spendPoint = histories.stream()
                .filter(h -> h.getType() == PointSpendType.SPEND)
                .mapToInt(PointHistory::getPoint)
                .sum();

        int totalPoint = earnedPoint - spendPoint;

        return new PointResponse(totalPoint);
    }
}
