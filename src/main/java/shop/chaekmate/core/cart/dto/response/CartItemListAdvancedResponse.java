package shop.chaekmate.core.cart.dto.response;

import java.util.List;

public record CartItemListAdvancedResponse(
        Long cartId,
        List<CartItemAdvancedResponse> items
) {
}
