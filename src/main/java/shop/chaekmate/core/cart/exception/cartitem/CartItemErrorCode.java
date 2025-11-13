package shop.chaekmate.core.cart.exception.cartitem;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import shop.chaekmate.core.common.exception.BaseErrorCode;

@Getter
public enum CartItemErrorCode implements BaseErrorCode {

    BOOK_NOT_FOUND(HttpStatus.BAD_REQUEST, "CART-ITEM-404", "해당 도서를 찾을 수 없습니다."),
    BOOK_INSUFFICIENT_STOCK(HttpStatus.BAD_REQUEST, "CART-ITEM-404", "해당 도서의 재고가 부족합니다."),
    CART_ITEM_INVALID_QUANTITY(HttpStatus.BAD_REQUEST, "CART-ITEM-400", "유효하지 않은 장바구니 아이템 수량입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    CartItemErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
