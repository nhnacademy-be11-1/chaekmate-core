package shop.chaekmate.core.external.aladin.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.ISBN;

import java.util.List;

public record AladinBookRegisterRequest(
        @NotBlank(message = "제목은 필수입니다.")
        String title,

        String index,

        String description,

        @NotBlank(message = "작가는 필수입니다.")
        String author,

        String publisher,

        @NotBlank(message = "출판일은 필수입니다.")
        String publishedAt,

        @NotNull(message = "ISBN은 필수입니다.")
        @ISBN(type = ISBN.Type.ISBN_13, message = "유효한 13자리 ISBN이어야 합니다.")
        String isbn,

        @NotNull(message = "정가는 필수입니다.")
        Integer price,

        @NotNull(message = "판매가는 필수입니다.")
        Integer salesPrice,

        @NotNull(message = "포장 여부 선택은 필수입니다.")
        Boolean isWrappable,

        @NotNull(message = "판매상태 여부는 필수입니다.")
        Boolean isSaleEnd,

        @NotNull(message = "이미지 URL은 필수입니다.")
        String imageUrl,

        @NotNull(message = "재고는 필수입니다.")
        Integer stock,

        List<Long> categoryIds,

        List<Long> tagIds
) {
}



