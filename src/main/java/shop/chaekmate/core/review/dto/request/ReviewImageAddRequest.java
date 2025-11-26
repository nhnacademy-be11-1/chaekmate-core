package shop.chaekmate.core.review.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Builder;

@Builder
public record ReviewImageAddRequest(
        @NotEmpty(message = "이미지 URL 목록은 비어 있을 수 없습니다.")
        List<String> imageUrls
) {
}
