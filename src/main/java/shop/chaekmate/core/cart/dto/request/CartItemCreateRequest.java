package shop.chaekmate.core.cart.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemCreateRequest(
        @NotNull
        Long bookId,

        @NotNull
        @PositiveOrZero
        int quantity
) {
}
