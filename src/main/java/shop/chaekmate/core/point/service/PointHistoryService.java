package shop.chaekmate.core.point.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Propagation;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryRepository pointHistoryRepository;
    private final MemberRepository memberRepository;

    //회원 포인트 적립
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreatePointHistoryResponse earnPointHistory(Long memberId, CreatePointHistoryRequest request) {
        log.info("[포인트 히스토리] 포인트 적립 시작 - 회원ID: {}, 포인트: {}, 사유: {}",
                memberId, request.point(), request.source());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("[포인트 히스토리] 회원을 찾을 수 없음 - 회원ID: {}", memberId);
                    return new MemberNotFoundException();
                });

        PointHistory history = new PointHistory(
                member,
                PointSpendType.EARN,
                request.point(),
                request.source()
        );

        log.info("[포인트 히스토리] PointHistory 엔티티 생성 완료 - 회원ID: {}, 포인트: {}", memberId, request.point());

        PointHistory saved = pointHistoryRepository.save(history);

        log.info("[포인트 히스토리] DB 저장 완료 - ID: {}, 회원ID: {}, 포인트: {}",
                saved.getId(), saved.getMember().getId(), saved.getPoint());

        CreatePointHistoryResponse response = new CreatePointHistoryResponse(
                saved.getId(),
                saved.getMember().getId(),
                saved.getType(),
                saved.getPoint(),
                saved.getSource()
        );

        log.info("[포인트 히스토리] 포인트 적립 완료 - 응답 ID: {}, 회원ID: {}, 포인트: {}",
                response.id(), response.member(), response.point());

        return response;
    }

    //회원 포인트 차감
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CreatePointHistoryResponse spendPointHistory(Long memberId, CreatePointHistoryRequest request) {
        log.info("[포인트 히스토리] 포인트 차감 시작 - 회원ID: {}, 포인트: {}, 사유: {}",
                memberId, request.point(), request.source());

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("[포인트 히스토리] 회원을 찾을 수 없음 - 회원ID: {}", memberId);
                    return new MemberNotFoundException();
                });

        PointHistory history = new PointHistory(
                member,
                PointSpendType.SPEND,
                request.point(),
                request.source()
        );

        log.info("[포인트 히스토리] PointHistory 엔티티 생성 완료 - 회원ID: {}, 포인트: {}", memberId, request.point());

        PointHistory saved = pointHistoryRepository.save(history);

        log.info("[포인트 히스토리] DB 저장 완료 - ID: {}, 회원ID: {}, 포인트: {}",
                saved.getId(), saved.getMember().getId(), saved.getPoint());

        CreatePointHistoryResponse response = new CreatePointHistoryResponse(
                saved.getId(),
                saved.getMember().getId(),
                saved.getType(),
                saved.getPoint(),
                saved.getSource()
        );

        log.info("[포인트 히스토리] 포인트 차감 완료 - 응답 ID: {}, 회원ID: {}, 포인트: {}",
                response.id(), response.member(), response.point());

        return response;
    }


    //포인트 history 조회 (전체)
    @Transactional(readOnly = true)
    public Page<PointHistoryResponse> getPointHistory(Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return pointHistoryRepository.findAll(sortedPageable)
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

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        return pointHistoryRepository.findByMemberId(memberId, sortedPageable)
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
