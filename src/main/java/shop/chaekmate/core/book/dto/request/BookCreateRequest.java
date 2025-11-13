package shop.chaekmate.core.book.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.ISBN;

import java.time.LocalDateTime;
import java.util.List;

public record BookCreateRequest(
        @NotBlank(message = "제목 등록은 필수입니다.")
        @Size(min = 1, max = 250, message = "제목은 1자 이상 250자 이하여야 합니다.")
        String title,
        String index,
        String description,

        @NotBlank(message = "작가 등록은 필수입니다.")
        String author,

        @Size(min = 1, max = 100, message = "출판사는 1자이상 100자 이하여야 합니다.")
        String publisher,

        LocalDateTime publishedAt,

        @NotNull(message = "isbn은 등록은 필수입니다.")
        @ISBN(type = ISBN.Type.ISBN_13, message = "유효한 13자리 ISBN 형식이 아닙니다.")
        String isbn,

        @NotNull(message = "정가 등록은 필수입니다.")
        Integer price,

        @NotNull(message = "판매가 등록은 필수입니다.")
        Integer salesPrice,

        @NotNull(message = "포장 여부 선택은 필수입니다.")
        Boolean isWrappable,

        @NotNull(message = "판매상태 여부는 필수입니다.")
        Boolean isSaleEnd,

        @NotNull(message = "재고 등록은 필수입니다.")
        Integer stock,

        List<Long> categoryIds,

        List<Long> tagIds
) {
}
