package shop.chaekmate.core.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL) // JSON 직렬화(응답으로 변환)할 때 null 값인 필드는 아예 제외
public record SuccessResponse<T>(
        LocalDateTime timestamp,
        String code,
        T data
) {
    public SuccessResponse(T data) {
        this(LocalDateTime.now(), "SUCCESS-200", data);
    }

    public static <T> SuccessResponse<T> of(T data) {
        return new SuccessResponse<>(data);
    }
}
