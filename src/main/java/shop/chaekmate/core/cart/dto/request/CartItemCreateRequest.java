package shop.chaekmate.core.cart.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CartItemCreateRequest(
        @NotBlank
        Long bookId
) {
}
