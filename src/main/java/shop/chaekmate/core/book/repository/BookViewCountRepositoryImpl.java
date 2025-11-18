package shop.chaekmate.core.book.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BookViewCountRepositoryImpl {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public int[] bulkUpdateViewCountOptimized(Map<Long, Long> viewMap) {
        if (viewMap == null || viewMap.isEmpty()) return new int[0];

        String sql = "UPDATE book SET views = views + ? WHERE id = ?";

        List<Object[]> batchArgs = new ArrayList<>();

        for (Map.Entry<Long, Long> entry : viewMap.entrySet()) {
            Long increment = entry.getValue(); // 증가량
            Long bookId = entry.getKey();      // 책 ID

            batchArgs.add(new Object[] { increment, bookId });
        }

        // JDBC Batch 실행
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }
}
