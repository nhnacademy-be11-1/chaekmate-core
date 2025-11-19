package shop.chaekmate.core.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record CartItemUpdateRequest(
        @NotNull
        @PositiveOrZero
        int quantity
) {
}
