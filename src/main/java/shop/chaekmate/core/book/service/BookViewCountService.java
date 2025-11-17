package shop.chaekmate.core.book.service;

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

        redisTemplate.opsForValue().increment(key);
    }

}
