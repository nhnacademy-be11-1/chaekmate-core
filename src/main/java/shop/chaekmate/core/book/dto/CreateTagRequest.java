package shop.chaekmate.core.book.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateTagRequest(@NotNull @Size(max = 255) String name) {
}
