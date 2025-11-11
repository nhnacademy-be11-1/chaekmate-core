package shop.chaekmate.core.book.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "도서 이미지 추가 요청 DTO")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BookImageAddRequest {

    @Schema(description = "이미지 URL", example = "https://example.com/image.jpg")
    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;
}
