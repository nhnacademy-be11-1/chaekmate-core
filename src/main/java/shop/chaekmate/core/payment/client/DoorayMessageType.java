package shop.chaekmate.core.payment.client;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DoorayMessageType {
    // 결제 관련
    PAYMENT_SUCCESS("🎉 결제가 완료되었습니다!", "#2ECC71"),
    PAYMENT_FAILED("❌ 결제에 실패했습니다.", "#E74C3C"),
    PAYMENT_CANCELED("🔄 결제가 취소되었습니다.", "#E67E22"),

    // 배송 관련
    SHIPPING_START("📦 상품이 배송을 시작했습니다!", "#3498DB"),
    SHIPPING_COMPLETE("📬 상품이 배송 완료되었습니다!", "#2ECC71"),

    // 반품 관련
    RETURN_REQUESTED("📦 반품 요청이 접수되었습니다.", "#3498DB"),
    RETURN_COMPLETED("👌 반품이 완료되었습니다!", "#9B59B6");

    private final String text;
    private final String color;

    public String text() { return text; }
    public String color() { return color; }
}
