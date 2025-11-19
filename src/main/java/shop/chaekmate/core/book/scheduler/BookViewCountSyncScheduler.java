package shop.chaekmate.core.book.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.chaekmate.core.book.repository.BookViewCountRepositoryImpl;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookViewCountSyncScheduler {

    private final StringRedisTemplate redisTemplate;
    private final BookViewCountRepositoryImpl viewCountRepository; // DB 업데이트용 Repository
    private static final String VIEW_KEY_PATTERN = "book:views:*";
    private static final String KEY_PREFIX = "book:views:";

    @Scheduled(fixedRate = 60_000) // 1분마다 실행
    public void syncViewCounts() {

        List<String> keys = new ArrayList<>();

        // SCAN을 사용해 Redis에서 'book:views:*' 패턴의 모든 Key 수집
        try (Cursor<String> cursor = redisTemplate.scan(
                ScanOptions.scanOptions().match(VIEW_KEY_PATTERN).count(500).build())) {
            while (cursor.hasNext()) {
                keys.add(cursor.next());
            }
        } catch (Exception e) {
            log.error("[ViewCountSync] Redis SCAN 실패", e);
            return;
        }

        if (keys.isEmpty()) return;

        // multiGet으로 한 번에 value 가져오기
        List<String> values = redisTemplate.opsForValue().multiGet(keys);
        if (values == null || values.size() != keys.size()) {
            log.error("[ViewCountSync] Redis multiGet 결과 오류: 요청된 키 수({})와 결과 수({}) 불일치",
                    keys.size(), values == null ? 0 : values.size());
            return;
        }

        Map<Long, Long> viewMap = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String valueStr = values.get(i);
            if (valueStr == null) continue;

            try {
                Long bookId = Long.parseLong(key.replace(KEY_PREFIX, ""));
                Long increment = Long.parseLong(valueStr);
                viewMap.put(bookId, increment);
            } catch (NumberFormatException e) {
                log.warn("[ViewCountSync] Redis Key/Value 파싱 오류: Key={}, Value={}", key, valueStr);
            }
        }

        if (viewMap.isEmpty()) return;

        // DB에 bulk update
        viewCountRepository.bulkUpdateViewCountOptimized(viewMap);

        // Redis에서 키 삭제
        Long deletedCount = redisTemplate.delete(keys);

        log.info("[ViewCountSync] 조회수 {}건 반영 완료. Redis에서 {}개의 키 삭제됨.", viewMap.size(), deletedCount);
    }
}
