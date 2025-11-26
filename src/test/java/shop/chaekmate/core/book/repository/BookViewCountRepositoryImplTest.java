package shop.chaekmate.core.book.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BookViewCountRepositoryImplTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    BookViewCountRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new BookViewCountRepositoryImpl(jdbcTemplate);
    }

    @Test
    void bulkUpdate_성공() {
        // given
        Map<Long, Long> viewMap = new HashMap<>();
        viewMap.put(1L, 10L);  // id=1, +10 증가
        viewMap.put(2L, 20L);  // id=2, +20 증가

        String expectedSql = "UPDATE book SET views = views + ? WHERE id = ?";

        int[] dbResult = {1, 1};

        // JdbcTemplate batchUpdate Mock
        when(jdbcTemplate.batchUpdate(eq(expectedSql), anyList()))
                .thenReturn(dbResult);

        // when
        int[] result = repository.bulkUpdateViewCountOptimized(viewMap);

        // then
        assertThat(result).containsExactly(1, 1);

        // SQL + 파라미터 검증
        ArgumentCaptor<List<Object[]>> captor = ArgumentCaptor.forClass(List.class);

        verify(jdbcTemplate, times(1))
                .batchUpdate(eq(expectedSql), captor.capture());

        List<Object[]> batchArgs = captor.getValue();

        assertThat(batchArgs).hasSize(2);

        // 1번째 row: increment=10L, id=1L
        assertThat(batchArgs.get(0)).containsExactly(10L, 1L);

        // 2번째 row: increment=20L, id=2L
        assertThat(batchArgs.get(1)).containsExactly(20L, 2L);
    }

    @Test
    void bulkUpdate_빈_map이면_DB호출없음() {
        // when
        int[] result = repository.bulkUpdateViewCountOptimized(Collections.emptyMap());

        // then
        assertThat(result).isEmpty();

        verify(jdbcTemplate, never()).batchUpdate(anyString(), anyList());
    }
}
