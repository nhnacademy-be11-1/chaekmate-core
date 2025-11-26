package shop.chaekmate.core.book.scheduler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ActiveProfiles;
import shop.chaekmate.core.book.repository.BookViewCountRepositoryImpl;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookViewCountSyncSchedulerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private BookViewCountRepositoryImpl viewCountRepository;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private BookViewCountSyncScheduler scheduler;

    @Test
    void 조회수_동기화_성공() {
        // given
        List<String> redisKeys = List.of("book:views:1", "book:views:2");

        // SCAN 커서 Mock
        Cursor<String> cursor = mock(Cursor.class);

        when(redisTemplate.scan(any()))
                .thenReturn(cursor);

        // 커서 작동 방식 제어
        when(cursor.hasNext()).thenReturn(true, true, false);
        when(cursor.next()).thenReturn("book:views:1", "book:views:2");

        // multiGet mock
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.multiGet(redisKeys))
                .thenReturn(List.of("10", "20"));

        // DB 업데이트 mock
        when(viewCountRepository.bulkUpdateViewCountOptimized(any()))
                .thenReturn(new int[]{1, 1});

        // Redis delete mock
        when(redisTemplate.delete(redisKeys))
                .thenReturn(2L);

        // when
        scheduler.syncViewCounts();

        // then
        // DB 업데이트 map 구성 검증
        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(viewCountRepository, times(1))
                .bulkUpdateViewCountOptimized(mapCaptor.capture());

        Map<Long, Long> viewMap = mapCaptor.getValue();

        // 값까지 검증
        assertThat(viewMap)
                .containsEntry(1L, 10L)
                .containsEntry(2L, 20L);

        // Redis 삭제 검증
        verify(redisTemplate, times(1)).delete(redisKeys);
    }
}
