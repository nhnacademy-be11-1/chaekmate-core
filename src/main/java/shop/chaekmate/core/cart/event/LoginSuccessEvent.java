package shop.chaekmate.core.cart.event;

public record LoginSuccessEvent(
        Long memberId,
        String guestId
) {
}
