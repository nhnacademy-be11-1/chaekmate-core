package shop.chaekmate.core.cart.exception.cartitem;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum CartItemErrorCode implements BaseErrorCode {

    INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "CART-ITEM-400", "유효하지 않은 장바구니 아이템 수량입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CartItemErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
