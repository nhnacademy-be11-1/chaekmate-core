package shop.chaekmate.core.book.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(Long parentCategoryId, @NotNull @Size(max = 255) String name) {
}
