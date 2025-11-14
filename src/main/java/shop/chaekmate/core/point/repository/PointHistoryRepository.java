package shop.chaekmate.core.point.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.chaekmate.core.point.entity.PointHistory;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<PointHistory> findByMemberId(Long memberId);

    Page<PointHistory> findByMemberId(Long memberId, Pageable pageable);

}