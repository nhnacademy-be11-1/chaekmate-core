package shop.chaekmate.core.book.service;

import java.time.Duration;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import shop.chaekmate.core.common.exception.CommonErrorCode;
import shop.chaekmate.core.common.exception.CoreException;

@Service
@RequiredArgsConstructor
public class BookViewCountService {

    private final StringRedisTemplate redisTemplate; // Key value 가 문자열로 고정
    private static final String PREFIX = "book:views:";

    public void increase(Long bookId) {

        if(Objects.isNull(bookId)) throw new CoreException(CommonErrorCode.BAD_REQUEST);

        String key = PREFIX + bookId;

        // 먼저 키를 0으로 초기화하고 TTL 5분 지정 (실제론 1분마다 DB반영하면서 삭제)
        redisTemplate.opsForValue().setIfAbsent(key, "0", Duration.ofMinutes(5));

        // 증가
        redisTemplate.opsForValue().increment(key);
    }

}
